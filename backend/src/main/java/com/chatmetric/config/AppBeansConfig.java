package com.chatmetric.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ChatMetricProperties.class)
public class AppBeansConfig {}

