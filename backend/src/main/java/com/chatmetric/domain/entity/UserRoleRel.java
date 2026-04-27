package com.chatmetric.domain.entity;

public class UserRoleRel {
  private Long userId;
  private String roleCode;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getRoleCode() {
    return roleCode;
  }

  public void setRoleCode(String roleCode) {
    this.roleCode = roleCode;
  }
}

