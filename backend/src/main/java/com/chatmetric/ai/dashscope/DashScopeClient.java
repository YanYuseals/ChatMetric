package com.chatmetric.ai.dashscope;

import com.chatmetric.config.ChatMetricProperties;
import com.chatmetric.exception.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * DashScope 调用封装（Embedding + Chat）。
 *
 * <p>说明：DashScope 的接口与返回字段可能随版本变化，这里用 JsonNode 做了“宽松解析”，并把 path/model 都做成可配置。
 */
@Component
public class DashScopeClient {
  private final ChatMetricProperties properties;
  private final RestClient restClient;
  private final ObjectMapper objectMapper;

  public DashScopeClient(ChatMetricProperties properties, ObjectMapper objectMapper) {
    this.properties = properties;
    this.objectMapper = objectMapper;
    this.restClient =
        RestClient.builder()
            .baseUrl(properties.getDashscope().getBaseUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .requestFactory(
                requestFactory -> {
                  requestFactory.setConnectTimeout(Duration.ofSeconds(10));
                  requestFactory.setReadTimeout(Duration.ofSeconds(60));
                })
            .build();
  }

  public List<Float> embedOne(String text) {
    if (properties.getDashscope().getApiKey() == null || properties.getDashscope().getApiKey().isBlank()) {
      throw new ApiException("未配置 DashScope API Key（chatmetric.dashscope.api-key 或环境变量 DASHSCOPE_API_KEY）");
    }
    String body =
        """
        {
          "model": "%s",
          "input": { "texts": [%s] }
        }
        """
            .formatted(
                jsonEscape(properties.getDashscope().getEmbeddingModel()), jsonEscape(text));

    String raw =
        restClient
            .post()
            .uri(properties.getDashscope().getEmbeddingPath())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getDashscope().getApiKey())
            .body(body)
            .retrieve()
            .body(String.class);
    try {
      JsonNode root = objectMapper.readTree(raw);
      JsonNode embeddingArray =
          firstNonNull(
              root.at("/output/embeddings/0/embedding"),
              root.at("/output/embeddings/0/vector"),
              root.at("/data/0/embedding"),
              root.at("/data/0/vector"));
      if (embeddingArray == null || !embeddingArray.isArray()) {
        throw new ApiException("DashScope embedding 返回解析失败: " + raw);
      }
      List<Float> vec = new ArrayList<>(embeddingArray.size());
      for (JsonNode n : embeddingArray) {
        vec.add(n.floatValue());
      }
      return vec;
    } catch (Exception e) {
      throw new ApiException("DashScope embedding 解析异常", e);
    }
  }

  public String chat(String prompt) {
    if (properties.getDashscope().getApiKey() == null || properties.getDashscope().getApiKey().isBlank()) {
      throw new ApiException("未配置 DashScope API Key（chatmetric.dashscope.api-key 或环境变量 DASHSCOPE_API_KEY）");
    }
    String body =
        """
        {
          "model": "%s",
          "input": { "prompt": %s },
          "parameters": { "result_format": "text" }
        }
        """
            .formatted(jsonEscape(properties.getDashscope().getChatModel()), jsonEscape(prompt));

    String raw =
        restClient
            .post()
            .uri(properties.getDashscope().getChatPath())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getDashscope().getApiKey())
            .body(body)
            .retrieve()
            .body(String.class);
    try {
      JsonNode root = objectMapper.readTree(raw);
      JsonNode textNode =
          firstNonNull(
              root.at("/output/text"),
              root.at("/output/choices/0/message/content"),
              root.at("/choices/0/message/content"),
              root.at("/choices/0/text"));
      if (textNode == null || textNode.isMissingNode()) {
        throw new ApiException("DashScope chat 返回解析失败: " + raw);
      }
      return textNode.asText();
    } catch (Exception e) {
      throw new ApiException("DashScope chat 解析异常", e);
    }
  }

  private static JsonNode firstNonNull(JsonNode... nodes) {
    for (JsonNode n : nodes) {
      if (n != null && !n.isMissingNode() && !n.isNull()) {
        return n;
      }
    }
    return null;
  }

  private static String jsonEscape(String s) {
    if (s == null) {
      return "null";
    }
    // 仅用于拼接 JSON，避免额外依赖；DashScope 文本一般较短。
    String escaped =
        s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "\\r")
            .replace("\n", "\\n");
    return "\"" + escaped + "\"";
  }
}
