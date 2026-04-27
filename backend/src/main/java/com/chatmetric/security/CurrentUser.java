package com.chatmetric.security;

public class CurrentUser {
  private final long userId;
  private final String username;
  private final String orgCode;
  private final String orgName;

  public CurrentUser(long userId, String username, String orgCode, String orgName) {
    this.userId = userId;
    this.username = username;
    this.orgCode = orgCode;
    this.orgName = orgName;
  }

  public long getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }

  public String getOrgCode() {
    return orgCode;
  }

  public String getOrgName() {
    return orgName;
  }
}

