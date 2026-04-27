package com.chatmetric.service;

import com.chatmetric.ai.vector.MetricType;
import com.chatmetric.ai.vector.MetricVectorDoc;
import com.chatmetric.ai.vector.MetricVectorStore;
import com.chatmetric.domain.entity.CustMetricDesc;
import com.chatmetric.domain.entity.OrgMetricDesc;
import com.chatmetric.domain.mapper.CustMetricDescMapper;
import com.chatmetric.domain.mapper.OrgMetricDescMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MetricVectorSyncService {
  private final OrgMetricDescMapper orgMetricDescMapper;
  private final CustMetricDescMapper custMetricDescMapper;
  private final MetricVectorStore metricVectorStore;

  public MetricVectorSyncService(
      OrgMetricDescMapper orgMetricDescMapper,
      CustMetricDescMapper custMetricDescMapper,
      MetricVectorStore metricVectorStore) {
    this.orgMetricDescMapper = orgMetricDescMapper;
    this.custMetricDescMapper = custMetricDescMapper;
    this.metricVectorStore = metricVectorStore;
  }

  public SyncResult syncAll() {
    List<MetricVectorDoc> docs = new ArrayList<>();

    for (OrgMetricDesc d : orgMetricDescMapper.findAll()) {
      String id = MetricType.ORG.name() + "_" + d.getMetricId();
      String logic = buildLogicText(d.getMetricName(), d.getMetricLogicDesc(), d.getTableName(), null);
      docs.add(new MetricVectorDoc(id, MetricType.ORG, d.getMetricId(), logic));
    }
    for (CustMetricDesc d : custMetricDescMapper.findAll()) {
      String id = MetricType.CUST.name() + "_" + d.getMetricId();
      String logic = buildLogicText(d.getMetricName(), d.getMetricLogicDesc(), d.getTableName(), d.getColumnName());
      docs.add(new MetricVectorDoc(id, MetricType.CUST, d.getMetricId(), logic));
    }

    metricVectorStore.upsert(docs);
    return new SyncResult(docs.size());
  }

  private static String buildLogicText(String metricName, String logicDesc, String tableName, String columnName) {
    StringBuilder sb = new StringBuilder();
    if (metricName != null && !metricName.isBlank()) {
      sb.append("指标名称：").append(metricName).append("\n");
    }
    if (logicDesc != null && !logicDesc.isBlank()) {
      sb.append("口径说明：").append(logicDesc).append("\n");
    }
    if (tableName != null && !tableName.isBlank()) {
      sb.append("表：").append(tableName).append("\n");
    }
    if (columnName != null && !columnName.isBlank()) {
      sb.append("列：").append(columnName).append("\n");
    }
    return sb.toString();
  }

  public static class SyncResult {
    private final int upsertCount;

    public SyncResult(int upsertCount) {
      this.upsertCount = upsertCount;
    }

    public int getUpsertCount() {
      return upsertCount;
    }
  }
}

