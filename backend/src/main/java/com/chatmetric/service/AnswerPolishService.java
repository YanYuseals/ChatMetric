package com.chatmetric.service;

import com.chatmetric.ai.dashscope.DashScopeClient;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AnswerPolishService {
  private final DashScopeClient dashScopeClient;

  public AnswerPolishService(DashScopeClient dashScopeClient) {
    this.dashScopeClient = dashScopeClient;
  }

  public String polishOrg(String question, OrgMetricQueryService.QueryResult result) {
    String prompt =
        """
        你是一个温柔、亲切的数据分析助手，请用中文回答用户问题，并适量使用 Emoji（不要过度）。
        你会拿到 SQL 与查询结果，请基于结果给出合理解读；如果结果为空，请礼貌说明可能原因与建议。

        用户问题：%s
        SQL：%s
        指标：%s（metric_id=%s）
        机构：%s
        查询结果：%s

        输出要求：
        - 先直接给结论（1-2 句）
        - 再补充 1-3 条解释/洞察（若有）
        - 不要编造不存在的数据
        """
            .formatted(
                safe(question),
                safe(result.getSql()),
                safe(result.getMetricName()),
                safe(result.getMetricId()),
                safe(result.getOrgCode()),
                safe(result.getDisplayValue()));
    try {
      return dashScopeClient.chat(prompt);
    } catch (Exception e) {
      // LLM 调用失败时给一个可用的降级输出
      return "我先把数据结果整理给你～\n\n"
          + "- 指标：" + safe(result.getMetricName()) + "（" + safe(result.getMetricId()) + "）\n"
          + "- 机构：" + safe(result.getOrgCode()) + "\n"
          + "- 结果：" + safe(result.getDisplayValue()) + "\n";
    }
  }

  public String polishCust(String question, CustMetricQueryService.QueryResult result) {
    String rowsPreview;
    if (result.getRows() == null || result.getRows().isEmpty()) {
      rowsPreview = "（无数据）";
    } else {
      rowsPreview =
          result.getRows().stream().limit(20).map(String::valueOf).collect(Collectors.joining(", "));
      if (result.getRows().size() > 20) {
        rowsPreview += " ...（共 " + result.getRows().size() + " 行）";
      }
    }

    String prompt =
        """
        你是一个温柔、亲切的数据分析助手，请用中文回答用户问题，并适量使用 Emoji（不要过度）。
        你会拿到 SQL 与查询结果（可能多行），请先总结，然后给出解释。

        用户问题：%s
        SQL：%s
        指标：%s（metric_id=%s）
        查询结果预览：%s

        输出要求：
        - 先总结结论
        - 若数据为空，说明可能原因
        - 不要编造不存在的数据
        """
            .formatted(
                safe(question), safe(result.getSql()), safe(result.getMetricName()), safe(result.getMetricId()), safe(rowsPreview));
    try {
      return dashScopeClient.chat(prompt);
    } catch (Exception e) {
      return "我先把查询结果整理给你～\n\n"
          + "- 指标：" + safe(result.getMetricName()) + "（" + safe(result.getMetricId()) + "）\n"
          + "- 行数：" + (result.getRows() == null ? 0 : result.getRows().size()) + "\n"
          + "- 预览：" + safe(rowsPreview) + "\n";
    }
  }

  private static String safe(String s) {
    return s == null ? "" : s;
  }
}
