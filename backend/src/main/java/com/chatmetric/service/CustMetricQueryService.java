package com.chatmetric.service;

import com.chatmetric.ai.vector.MetricType;
import com.chatmetric.ai.vector.MetricVectorSearchHit;
import com.chatmetric.ai.vector.MetricVectorStore;
import com.chatmetric.domain.entity.CustMetricDesc;
import com.chatmetric.domain.mapper.CustMetricDescMapper;
import com.chatmetric.exception.ApiException;
import com.chatmetric.util.SqlSafe;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustMetricQueryService {
  private final MetricVectorStore metricVectorStore;
  private final CustMetricDescMapper custMetricDescMapper;
  private final JdbcTemplate jdbcTemplate;

  public CustMetricQueryService(
      MetricVectorStore metricVectorStore, CustMetricDescMapper custMetricDescMapper, JdbcTemplate jdbcTemplate) {
    this.metricVectorStore = metricVectorStore;
    this.custMetricDescMapper = custMetricDescMapper;
    this.jdbcTemplate = jdbcTemplate;
  }

  public QueryResult query(String question, LocalDate dt) {
    MetricVectorSearchHit hit = metricVectorStore.searchTop1(MetricType.CUST, question);
    if (hit == null) {
      throw new ApiException("未在指标库中匹配到相关客户指标，请先在管理页同步指标元数据");
    }
    CustMetricDesc desc = custMetricDescMapper.findByMetricId(hit.getMetricId());
    if (desc == null) {
      throw new ApiException("匹配到的 metric_id 在 MySQL 中不存在: " + hit.getMetricId());
    }

    String table = SqlSafe.safeName(desc.getTableName(), "table_name");
    String col = SqlSafe.safeName(desc.getColumnName(), "column_name");
    String sql = "SELECT `" + col + "` FROM `" + table + "` WHERE dt = ? LIMIT 200";

    List<Object> rows =
        jdbcTemplate.query(
            sql,
            ps -> ps.setObject(1, dt),
            (rs, rowNum) -> rs.getObject(1));

    return new QueryResult(sql, desc.getMetricName(), desc.getMetricId(), rows);
  }

  public static class QueryResult {
    private final String sql;
    private final String metricName;
    private final String metricId;
    private final List<Object> rows;

    public QueryResult(String sql, String metricName, String metricId, List<Object> rows) {
      this.sql = sql;
      this.metricName = metricName;
      this.metricId = metricId;
      this.rows = rows;
    }

    public String getSql() {
      return sql;
    }

    public String getMetricName() {
      return metricName;
    }

    public String getMetricId() {
      return metricId;
    }

    public List<Object> getRows() {
      return rows;
    }
  }
}

