package com.chatmetric.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ChatCustQueryRequest {
  @NotBlank private String question;
  @NotNull private LocalDate dt;

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public LocalDate getDt() {
    return dt;
  }

  public void setDt(LocalDate dt) {
    this.dt = dt;
  }
}

