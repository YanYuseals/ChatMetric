package com.chatmetric.security;

import com.chatmetric.domain.entity.User;
import com.chatmetric.domain.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserContextInterceptor implements HandlerInterceptor {
  private final UserMapper userMapper;
  private String userIdHeader = "X-User-Id";

  public UserContextInterceptor(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  public void setUserIdHeader(String userIdHeader) {
    this.userIdHeader = userIdHeader;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String userIdStr = request.getHeader(userIdHeader);
    if (userIdStr == null || userIdStr.isBlank()) {
      return true;
    }
    try {
      long userId = Long.parseLong(userIdStr);
      User user = userMapper.findById(userId);
      if (user != null) {
        CurrentUserHolder.set(new CurrentUser(user.getId(), user.getUsername(), user.getOrgCode(), user.getOrgName()));
      }
    } catch (NumberFormatException ignored) {
      // ignore invalid header
    }
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    CurrentUserHolder.clear();
  }
}

