package com.chatmetric.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ChatOrgQueryRequest {
  @NotBlank private String question;
  @NotNull private LocalDate dataDate;

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public LocalDate getDataDate() {
    return dataDate;
  }

  public void setDataDate(LocalDate dataDate) {
    this.dataDate = dataDate;
  }
}

