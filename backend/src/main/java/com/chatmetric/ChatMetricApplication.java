package com.chatmetric;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.chatmetric.domain.mapper")
public class ChatMetricApplication {
  public static void main(String[] args) {
    SpringApplication.run(ChatMetricApplication.class, args);
  }
}

