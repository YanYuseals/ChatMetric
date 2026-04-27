package com.chatmetric.ai.vector;

public class MetricVectorSearchHit {
  private final String id;
  private final MetricType type;
  private final String metricId;
  private final double score;

  public MetricVectorSearchHit(String id, MetricType type, String metricId, double score) {
    this.id = id;
    this.type = type;
    this.metricId = metricId;
    this.score = score;
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

  public double getScore() {
    return score;
  }
}
