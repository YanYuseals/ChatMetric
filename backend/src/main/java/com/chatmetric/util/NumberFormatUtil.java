package com.chatmetric.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public final class NumberFormatUtil {
  private NumberFormatUtil() {}

  public static String formatThousand(BigDecimal value) {
    if (value == null) {
      return "-";
    }
    // 保留最多 8 位小数，末尾 0 自动去除
    DecimalFormat df = new DecimalFormat("#,##0.########");
    df.setGroupingUsed(true);
    return df.format(value);
  }
}

