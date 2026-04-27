package com.chatmetric.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrgMetricData {
  private LocalDate dataDate;
  private String orgCode;
  private String orgName;
  private String metricId;
  private String metricName;
  private BigDecimal metricValue;

  public LocalDate getDataDate() {
    return dataDate;
  }

  public void setDataDate(LocalDate dataDate) {
    this.dataDate = dataDate;
  }

  public String getOrgCode() {
    return orgCode;
  }

  public void setOrgCode(String orgCode) {
    this.orgCode = orgCode;
  }

  public String getOrgName() {
    return orgName;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

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

  public BigDecimal getMetricValue() {
    return metricValue;
  }

  public void setMetricValue(BigDecimal metricValue) {
    this.metricValue = metricValue;
  }
}

