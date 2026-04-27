package com.chatmetric.config;

import com.chatmetric.security.UserContextInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  private final UserContextInterceptor userContextInterceptor;

  public WebConfig(
      UserContextInterceptor userContextInterceptor,
      @Value("${chatmetric.security.user-id-header:X-User-Id}") String userIdHeader) {
    this.userContextInterceptor = userContextInterceptor;
    this.userContextInterceptor.setUserIdHeader(userIdHeader);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(userContextInterceptor).addPathPatterns("/**");
  }
}

