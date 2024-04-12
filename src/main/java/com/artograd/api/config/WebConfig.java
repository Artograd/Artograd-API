package com.artograd.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${artograd.env:cloud}")
  private String env;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    if (env != null && env.equals("local")) {
      registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
    }
  }
}
