package com.artograd.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "artograd")
@Getter
@Setter
public class ArtogradProperties {
  private String env;
  private String name;
  private String link;
}