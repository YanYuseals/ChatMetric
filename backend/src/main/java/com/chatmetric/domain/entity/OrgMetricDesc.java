package com.chatmetric.domain.entity;

public class OrgMetricDesc {
  private String metricId;
  private String metricName;
  private String tableName;
  private String metricLogicDesc;

  public String getMetricId() {
    return metricId;
  }

  public void setMetricId(String metricId) {
    this.metricId = metricId;
  }

  public String getMetricName() {
    return metricName;
  }

  public void setMetricName(String metricName) {
    this.metricName = metricName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getMetricLogicDesc() {
    return metricLogicDesc;
  }

  public void setMetricLogicDesc(String metricLogicDesc) {
    this.metricLogicDesc = metricLogicDesc;
  }
}

