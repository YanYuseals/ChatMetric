package com.chatmetric.ai.vector;

public class MetricVectorDoc {
  private final String id;
  private final MetricType type;
  private final String metricId;
  private final String logicDesc;

  public MetricVectorDoc(
      String id,
      MetricType type,
      String metricId,
      String logicDesc) {
    this.id = id;
    this.type = type;
    this.metricId = metricId;
    this.logicDesc = logicDesc;
  }

  public String getId() {
    return id;
  }

  public MetricType getType() {
    return type;
  }

  public String getMetricId() {
    return metricId;
  }

  public String getLogicDesc() {
    return logicDesc;
  }
}
