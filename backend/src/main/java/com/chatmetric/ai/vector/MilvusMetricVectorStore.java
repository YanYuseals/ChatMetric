package com.chatmetric.ai.vector;

import com.chatmetric.ai.dashscope.DashScopeClient;
import com.chatmetric.config.ChatMetricProperties;
import com.chatmetric.exception.ApiException;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.SearchResults;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FlushParam;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.partition.CreatePartitionParam;
import io.milvus.param.partition.HasPartitionParam;
import io.milvus.response.SearchResultsWrapper;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Milvus 向量存储实现（仅存储向量 + 主键），命中后再回查 MySQL 取 table/column 等元数据。
 *
 * <p>Collection fields：id(VarChar PK), vector(FloatVector)
 * <p>Partitions：ORG / CUST
 */
@Component
public class MilvusMetricVectorStore implements MetricVectorStore {
  private static final String FIELD_ID = "id";
  private static final String FIELD_VECTOR = "vector";

  private final ChatMetricProperties props;
  private final DashScopeClient dashScopeClient;
  private MilvusServiceClient milvus;

  public MilvusMetricVectorStore(ChatMetricProperties props, DashScopeClient dashScopeClient) {
    this.props = props;
    this.dashScopeClient = dashScopeClient;
  }

  @PostConstruct
  public void init() {
    this.milvus =
        new MilvusServiceClient(
            ConnectParam.newBuilder()
                .withHost(props.getMilvus().getHost())
                .withPort(props.getMilvus().getPort())
                .build());
    ensureCollectionAndPartitions();
  }

  private void ensureCollectionAndPartitions() {
    String collection = props.getMilvus().getCollection();
    R<Boolean> has = milvus.hasCollection(HasCollectionParam.newBuilder().withCollectionName(collection).build());
    if (has.getData() == null || !has.getData()) {
      int dim = props.getMilvus().getDimension();
      CreateCollectionParam create =
          CreateCollectionParam.newBuilder()
              .withCollectionName(collection)
              .withDescription("ChatMetric metric description vectors")
              .withShardsNum(2)
              .addFieldType(
                  io.milvus.param.FieldType.newBuilder()
                      .withName(FIELD_ID)
                      .withDataType(DataType.VarChar)
                      .withMaxLength(128)
                      .withPrimaryKey(true)
                      .withAutoID(false)
                      .build())
              .addFieldType(
                  io.milvus.param.FieldType.newBuilder()
                      .withName(FIELD_VECTOR)
                      .withDataType(DataType.FloatVector)
                      .withDimension(dim)
                      .build())
              .build();
      milvus.createCollection(create);

      Map<String, Object> extra = new HashMap<>();
      extra.put("M", 16);
      extra.put("efConstruction", 200);
      milvus.createIndex(
          CreateIndexParam.newBuilder()
              .withCollectionName(collection)
              .withFieldName(FIELD_VECTOR)
              .withIndexType(IndexType.HNSW)
              .withMetricType(io.milvus.param.MetricType.COSINE)
              .withExtraParam(extra)
              .build());
    }

    ensurePartition(collection, MetricType.ORG.name());
    ensurePartition(collection, MetricType.CUST.name());
  }

  private void ensurePartition(String collection, String partition) {
    R<Boolean> hasPartition =
        milvus.hasPartition(
            HasPartitionParam.newBuilder().withCollectionName(collection).withPartitionName(partition).build());
    if (hasPartition.getData() != null && hasPartition.getData()) {
      return;
    }
    milvus.createPartition(
        CreatePartitionParam.newBuilder().withCollectionName(collection).withPartitionName(partition).build());
  }

  @Override
  public void upsert(List<MetricVectorDoc> docs) {
    if (docs == null || docs.isEmpty()) {
      return;
    }
    String collection = props.getMilvus().getCollection();

    // Milvus 不支持真正的 upsert：先 delete 再 insert
    for (MetricVectorDoc doc : docs) {
      String expr = FIELD_ID + " == \"" + escapeExpr(doc.getId()) + "\"";
      milvus.delete(DeleteParam.newBuilder().withCollectionName(collection).withExpr(expr).build());
    }

    // 按 partition 分组插入
    Map<MetricType, List<MetricVectorDoc>> grouped = new HashMap<>();
    for (MetricVectorDoc d : docs) {
      grouped.computeIfAbsent(d.getType(), k -> new ArrayList<>()).add(d);
    }
    for (Map.Entry<MetricType, List<MetricVectorDoc>> entry : grouped.entrySet()) {
      MetricType type = entry.getKey();
      List<MetricVectorDoc> partDocs = entry.getValue();

      List<String> ids = new ArrayList<>(partDocs.size());
      List<List<Float>> vectors = new ArrayList<>(partDocs.size());
      for (MetricVectorDoc d : partDocs) {
        ids.add(d.getId());
        vectors.add(dashScopeClient.embedOne(d.getLogicDesc()));
      }

      List<InsertParam.Field> fields = new ArrayList<>();
      fields.add(new InsertParam.Field(FIELD_ID, ids));
      fields.add(new InsertParam.Field(FIELD_VECTOR, vectors));

      R<?> r =
          milvus.insert(
              InsertParam.newBuilder()
                  .withCollectionName(collection)
                  .withPartitionName(type.name())
                  .withFields(fields)
                  .build());
      if (r.getStatus() != R.Status.Success.getCode()) {
        throw new ApiException("Milvus insert 失败: " + r.getMessage());
      }
    }

    milvus.flush(FlushParam.newBuilder().addCollectionName(collection).build());
  }

  @Override
  public MetricVectorSearchHit searchTop1(MetricType type, String queryText) {
    String collection = props.getMilvus().getCollection();
    List<Float> queryVec = dashScopeClient.embedOne(queryText);

    SearchParam search =
        SearchParam.newBuilder()
            .withCollectionName(collection)
            .withPartitionNames(List.of(type.name()))
            .withMetricType(io.milvus.param.MetricType.COSINE)
            .withTopK(1)
            .withVectors(List.of(queryVec))
            .withVectorFieldName(FIELD_VECTOR)
            .withOutFields(List.of(FIELD_ID))
            .withParams("{\"ef\":64}")
            .build();

    R<SearchResults> res = milvus.search(search);
    if (res.getStatus() != R.Status.Success.getCode()) {
      throw new ApiException("Milvus search 失败: " + res.getMessage());
    }
    SearchResultsWrapper w = new SearchResultsWrapper(res.getData().getResults());
    List<SearchResultsWrapper.IDScore> scores = w.getIDScore(0);
    if (scores == null || scores.isEmpty()) {
      return null;
    }
    SearchResultsWrapper.IDScore top = scores.get(0);
    String id = top.getStrID();
    if (id == null || id.isBlank()) {
      id = String.valueOf(top.getLongID());
    }
    String metricId = parseMetricId(type, id);
    return new MetricVectorSearchHit(id, type, metricId, top.getScore());
  }

  private static String parseMetricId(MetricType type, String id) {
    String prefix = type.name() + "_";
    if (id != null && id.startsWith(prefix)) {
      return id.substring(prefix.length());
    }
    return id;
  }

  private static String escapeExpr(String s) {
    return s.replace("\\", "\\\\").replace("\"", "\\\"");
  }
}

