package com.artograd.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application")
@Getter
@Setter
public class ApplicationProperties {

  private Security security;
  private Servlet servlet;
  private Data data;

  @Getter
  @Setter
  public static class Security {
    private OAuth2 oauth2;

    @Getter
    @Setter
    public static class OAuth2 {
      private ResourceServer resourceserver;

      @Getter
      @Setter
      public static class ResourceServer {
        private Jwt jwt;

        @Getter
        @Setter
        public static class Jwt {
          private String issuerUri;
        }
      }
    }
  }

  @Getter
  @Setter
  public static class Servlet {
    private Multipart multipart;

    @Getter
    @Setter
    public static class Multipart {
      private String maxFileSize;
      private String maxRequestSize;
    }
  }

  @Getter
  @Setter
  public static class Data {
    private MongoDB mongodb;

    @Getter
    @Setter
    public static class MongoDB {
      private String uri;
    }
  }
}
