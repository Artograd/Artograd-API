package com.artograd.api.services.impl;

import com.artograd.api.config.AwsProperties;
import com.artograd.api.model.EmailDetails;
import com.artograd.api.model.SocialMediaContact;
import com.artograd.api.model.Tender;
import com.artograd.api.services.IEmailService;
import com.artograd.api.services.IEmailTemplates;
import com.artograd.api.services.ISocialMediaContactService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class EmailService implements IEmailService {

  @Autowired private MessageSource messageSource;

  @Autowired private ISocialMediaContactService socialMediaContactService;

  @Autowired private AwsProperties awsProperties;

  private final SqsClient sqsClient;

  @Autowired private IEmailTemplates templatesEngine;

  public EmailService() {
    this.sqsClient = SqsClient.builder().build();
  }

  private void sendToQueue(String recipientEmail, String subject, String body, String groupId) {
    EmailDetails emailDetails = new EmailDetails(recipientEmail, subject, body);
    String emailDetailsString = convertToJson(emailDetails);

    SendMessageRequest sendMsgRequest =
        SendMessageRequest.builder()
            .queueUrl(awsProperties.getSqs().getMails())
            .messageBody(emailDetailsString)
            .messageGroupId(groupId)
            .build();

    sqsClient.sendMessage(sendMsgRequest);
  }

  private String convertToJson(EmailDetails emailDetails) {
    try {
      return new ObjectMapper().writeValueAsString(emailDetails);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return "";
  }

  @Override
  public void sendEmailsTenderIsPublished(Tender tender) {
    List<SocialMediaContact> contacts =
        socialMediaContactService.getContactsByUserId(tender.getOwnerId());

    for (SocialMediaContact contact : contacts) {
      String body = templatesEngine.getTenderPublicationTemplate(tender, contact);
      String subjectKey = "tender.published";
      @SuppressWarnings("deprecation")
      String subject =
          messageSource.getMessage(subjectKey, null, new Locale(contact.getContactLanguage()));
      if (contact.isActive()) {
        sendToQueue(contact.getContactEmail(), subject, body, subjectKey);
      }
    }
  }
}
