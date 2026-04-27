package com.chatmetric.ai.vector;

import java.util.List;

public interface MetricVectorStore {
  void upsert(List<MetricVectorDoc> docs);

  MetricVectorSearchHit searchTop1(MetricType type, String queryText);
}

