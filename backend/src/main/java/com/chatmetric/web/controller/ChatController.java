package com.chatmetric.web.controller;

import com.chatmetric.service.AnswerPolishService;
import com.chatmetric.service.CustMetricQueryService;
import com.chatmetric.service.OrgMetricQueryService;
import com.chatmetric.web.dto.ChatCustQueryRequest;
import com.chatmetric.web.dto.ChatOrgQueryRequest;
import com.chatmetric.web.dto.ChatResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
  private final OrgMetricQueryService orgMetricQueryService;
  private final CustMetricQueryService custMetricQueryService;
  private final AnswerPolishService answerPolishService;

  public ChatController(
      OrgMetricQueryService orgMetricQueryService,
      CustMetricQueryService custMetricQueryService,
      AnswerPolishService answerPolishService) {
    this.orgMetricQueryService = orgMetricQueryService;
    this.custMetricQueryService = custMetricQueryService;
    this.answerPolishService = answerPolishService;
  }

  @PostMapping("/org")
  public ChatResponse chatOrg(@Valid @RequestBody ChatOrgQueryRequest req) {
    OrgMetricQueryService.QueryResult result = orgMetricQueryService.query(req.getQuestion(), req.getDataDate());
    String answer = answerPolishService.polishOrg(req.getQuestion(), result);
    return new ChatResponse(answer, result.getSql());
  }

  @PostMapping("/cust")
  public ChatResponse chatCust(@Valid @RequestBody ChatCustQueryRequest req) {
    CustMetricQueryService.QueryResult result = custMetricQueryService.query(req.getQuestion(), req.getDt());
    String answer = answerPolishService.polishCust(req.getQuestion(), result);
    return new ChatResponse(answer, result.getSql());
  }
}

