package com.artograd.api;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.artograd.api.model.Tender;
import com.artograd.api.model.enums.TenderStatus;
import com.artograd.api.repositories.TenderRepository;
import java.util.Date;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TenderStatusUpdateLambdaHandler
    implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

  private TenderRepository tenderRepository;

  private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

  static {
    try {
      handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class);
    } catch (ContainerInitializationException ex) {
      throw new RuntimeException("Unable to load spring boot application", ex);
    }
  }

  public TenderStatusUpdateLambdaHandler() {
    ApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
    this.tenderRepository = context.getBean(TenderRepository.class);
  }

  @Override
  public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
    AwsProxyResponse response = handler.proxy(awsProxyRequest, context);
    Date today = new Date();
    List<Tender> tenders =
        tenderRepository.findBySubmissionStartAndStatus(today, TenderStatus.PUBLISHED.toString());

    for (Tender tender : tenders) {
      tender.setStatus(TenderStatus.IDEATION.toString());
      tenderRepository.save(tender);
    }
    return response;
  }
}
