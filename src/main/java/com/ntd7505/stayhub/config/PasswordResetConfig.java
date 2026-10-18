package com.ntd7505.stayhub.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
  AppMailProperties.class,
  FrontendProperties.class,
  PasswordResetProperties.class
})
public class PasswordResetConfig {}
