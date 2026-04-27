package com.chatmetric.util;

import com.chatmetric.exception.ApiException;

public final class SqlSafe {
  private SqlSafe() {}

  public static String safeName(String name, String what) {
    if (name == null || name.isBlank()) {
      throw new ApiException(what + " 不能为空");
    }
    // 仅允许 a-zA-Z0-9_，避免动态表名/列名注入
    if (!name.matches("^[A-Za-z0-9_]+$")) {
      throw new ApiException(what + " 非法: " + name);
    }
    return name;
  }
}

