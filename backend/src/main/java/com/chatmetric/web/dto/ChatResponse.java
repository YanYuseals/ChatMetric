package com.chatmetric.web.dto;

public class ChatResponse {
  private final String answer;
  private final String sql;

  public ChatResponse(String answer, String sql) {
    this.answer = answer;
    this.sql = sql;
  }

  public String getAnswer() {
    return answer;
  }

  public String getSql() {
    return sql;
  }
}

