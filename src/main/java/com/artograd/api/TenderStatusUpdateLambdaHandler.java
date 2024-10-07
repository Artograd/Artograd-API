package com.artograd.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.artograd.api.model.Tender;
import com.artograd.api.model.enums.TenderStatus;
import com.artograd.api.repositories.TenderRepository;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TenderStatusUpdateLambdaHandler implements RequestHandler<ScheduledEvent, String> {

  private static final ApplicationContext context;

  static {
    context = new AnnotationConfigApplicationContext(Application.class);
  }

  @Autowired private TenderRepository tenderRepository;

  @Override
  public String handleRequest(ScheduledEvent event, Context context) {
    Date today = new Date();
    List<Tender> tenders =
        tenderRepository.findBySubmissionStartAndStatus(today, TenderStatus.PUBLISHED.toString());

    for (Tender tender : tenders) {
      tender.setStatus(TenderStatus.IDEATION.toString());
      tenderRepository.save(tender);
    }

    return "Tenders updated successfully";
  }
}
