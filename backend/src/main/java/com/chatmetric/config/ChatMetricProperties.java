package com.chatmetric.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chatmetric")
public class ChatMetricProperties {
  private final DashScope dashscope = new DashScope();
  private final Milvus milvus = new Milvus();

  public DashScope getDashscope() {
    return dashscope;
  }

  public Milvus getMilvus() {
    return milvus;
  }

  public static class DashScope {
    private String baseUrl = "https://dashscope.aliyuncs.com";
    private String apiKey;
    private String chatModel = "qwen-plus";
    private String embeddingModel = "text-embedding-v2";
    private String chatPath = "/api/v1/services/aigc/text-generation/generation";
    private String embeddingPath = "/api/v1/services/embeddings/text-embedding/text-embedding";

    public String getBaseUrl() {
      return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }

    public String getApiKey() {
      return apiKey;
    }

    public void setApiKey(String apiKey) {
      this.apiKey = apiKey;
    }

    public String getChatModel() {
      return chatModel;
    }

    public void setChatModel(String chatModel) {
      this.chatModel = chatModel;
    }

    public String getEmbeddingModel() {
      return embeddingModel;
    }

    public void setEmbeddingModel(String embeddingModel) {
      this.embeddingModel = embeddingModel;
    }

    public String getChatPath() {
      return chatPath;
    }

    public void setChatPath(String chatPath) {
      this.chatPath = chatPath;
    }

    public String getEmbeddingPath() {
      return embeddingPath;
    }

    public void setEmbeddingPath(String embeddingPath) {
      this.embeddingPath = embeddingPath;
    }
  }

  public static class Milvus {
    private String host = "127.0.0.1";
    private int port = 19530;
    private String collection = "metric_desc";
    private int dimension = 1536;

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public String getCollection() {
      return collection;
    }

    public void setCollection(String collection) {
      this.collection = collection;
    }

    public int getDimension() {
      return dimension;
    }

    public void setDimension(int dimension) {
      this.dimension = dimension;
    }
  }
}

