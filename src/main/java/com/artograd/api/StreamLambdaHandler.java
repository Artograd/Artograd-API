package com.artograd.api;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class StreamLambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

  private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

  static {
    try {
      handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class);
    } catch (ContainerInitializationException ex) {
      throw new RuntimeException("Unable to load spring boot application", ex);
    }
  }

  @Override
  public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
    AwsProxyResponse response = handler.proxy(awsProxyRequest, context);
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader("Access-Control-Allow-Credentials", "true");
    return response;
  }
}
