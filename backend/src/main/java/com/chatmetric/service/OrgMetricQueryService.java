package com.chatmetric.service;

import com.chatmetric.ai.vector.MetricType;
import com.chatmetric.ai.vector.MetricVectorSearchHit;
import com.chatmetric.ai.vector.MetricVectorStore;
import com.chatmetric.domain.entity.OrgMetricDesc;
import com.chatmetric.domain.mapper.OrgMetricDescMapper;
import com.chatmetric.exception.ApiException;
import com.chatmetric.security.CurrentUser;
import com.chatmetric.security.CurrentUserHolder;
import com.chatmetric.util.NumberFormatUtil;
import com.chatmetric.util.SqlSafe;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrgMetricQueryService {
  private final MetricVectorStore metricVectorStore;
  private final OrgMetricDescMapper orgMetricDescMapper;
  private final JdbcTemplate jdbcTemplate;

  public OrgMetricQueryService(
      MetricVectorStore metricVectorStore, OrgMetricDescMapper orgMetricDescMapper, JdbcTemplate jdbcTemplate) {
    this.metricVectorStore = metricVectorStore;
    this.orgMetricDescMapper = orgMetricDescMapper;
    this.jdbcTemplate = jdbcTemplate;
  }

  public QueryResult query(String question, LocalDate dataDate) {
    CurrentUser user = CurrentUserHolder.get();
    if (user == null) {
      throw new ApiException("未登录：请在请求头中携带 X-User-Id");
    }
    MetricVectorSearchHit hit = metricVectorStore.searchTop1(MetricType.ORG, question);
    if (hit == null) {
      throw new ApiException("未在指标库中匹配到相关机构指标，请先在管理页同步指标元数据");
    }
    OrgMetricDesc desc = orgMetricDescMapper.findByMetricId(hit.getMetricId());
    if (desc == null) {
      throw new ApiException("匹配到的 metric_id 在 MySQL 中不存在: " + hit.getMetricId());
    }

    String tableName = SqlSafe.safeName(desc.getTableName(), "table_name");
    String sql =
        "SELECT metric_value FROM `"
            + tableName
            + "` WHERE metric_id = ? AND org_code = ? AND data_date = ?";

    BigDecimal value =
        jdbcTemplate.query(
            sql,
            ps -> {
              ps.setString(1, desc.getMetricId());
              ps.setString(2, user.getOrgCode());
              ps.setObject(3, dataDate);
            },
            rs -> rs.next() ? rs.getBigDecimal(1) : null);

    if (value == null) {
      return new QueryResult(
          sql,
          "没有查询到数据（可能该日期或机构无该指标数据）。",
          null,
          desc.getMetricName(),
          desc.getMetricId(),
          user.getOrgCode());
    }
    String formatted = NumberFormatUtil.formatThousand(value);
    return new QueryResult(sql, formatted, value, desc.getMetricName(), desc.getMetricId(), user.getOrgCode());
  }

  public static class QueryResult {
    private final String sql;
    private final String displayValue;
    private final BigDecimal rawValue;
    private final String metricName;
    private final String metricId;
    private final String orgCode;

    public QueryResult(
        String sql,
        String displayValue,
        BigDecimal rawValue,
        String metricName,
        String metricId,
        String orgCode) {
      this.sql = sql;
      this.displayValue = displayValue;
      this.rawValue = rawValue;
      this.metricName = metricName;
      this.metricId = metricId;
      this.orgCode = orgCode;
    }

    public String getSql() {
      return sql;
    }

    public String getDisplayValue() {
      return displayValue;
    }

    public BigDecimal getRawValue() {
      return rawValue;
    }

    public String getMetricName() {
      return metricName;
    }

    public String getMetricId() {
      return metricId;
    }

    public String getOrgCode() {
      return orgCode;
    }
  }
}

