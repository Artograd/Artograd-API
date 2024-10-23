package com.artograd.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AwsProperties {

  private S3 s3;
  private Cloudfront cloudfront;
  private Cognito cognito;
  private Sqs sqs;

  // Getters and Setters
  @Getter
  @Setter
  public static class S3 {
    private String bucketName;
  }

  @Getter
  @Setter
  public static class Cloudfront {
    private String distributionDomain;
  }

  @Getter
  @Setter
  public static class Cognito {
    private String userPoolId;
  }

  @Getter
  @Setter
  public static class Sqs {
    private String mails;
  }
}
