package com.artograd.api.services.impl;

import com.artograd.api.model.SocialMediaContact;
import com.artograd.api.model.Tender;
import com.artograd.api.model.enums.UserAttributeKey;
import com.artograd.api.services.IEmailTemplates;
import com.artograd.api.services.IUserService;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


@Service
public class EmailTemplates implements IEmailTemplates {
  private final TemplateEngine templateEngine;

  @Value("${artograd.name}")
  private String platformName;

  @Value("${artograd.link}")
  private String platformLink;

  @Autowired
  private IUserService userService;

  /**
   * Empty constructor.
   */
  public EmailTemplates() {
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setSuffix(".html");
    templateResolver.setPrefix("templates/");
    templateResolver.setTemplateMode(TemplateMode.HTML);

    templateEngine = new TemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);
  }

  @Override
  public String getTenderPublicationTemplate(Tender tender, SocialMediaContact contact) {
    Context context = new Context();
    fillBaseParams(context, contact);
    fillStateOfficerParams(context, tender.getOwnerId());
    fillTenderParams(context, tender);

    String body = templateEngine.process("tender_published_" 
          + contact.getContactLanguage(), context);
    return applyVariables(context, body);
  }

  private void fillBaseParams(Context context, SocialMediaContact contact) {
    context.setVariable("emailStyle", templateEngine.process("email_styles", new Context()));
    context.setVariable("userName", contact.getContactName());
    context.setVariable("platformName", platformName);
    context.setVariable("platformLink", platformLink);
  }

  private void fillTenderParams(Context context, Tender tender) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
    context.setVariable("tenderTitle", tender.getTitle());
    context.setVariable("submissionPeriodFrom", dateFormat.format(tender.getSubmissionStart()));
    context.setVariable("submissionPeriodTo", dateFormat.format(tender.getSubmissionEnd()));
    context.setVariable("tenderLink", platformLink + "/tender/" + tender.getId());
  }

  private void fillStateOfficerParams(Context context, String ownerId) {
    userService.getUserByUsername(ownerId).ifPresent(user -> {
      context.setVariable("stateOfficerName",
          user.getAttributeByKey(UserAttributeKey.GIVEN_NAME) 
          + " " + user.getAttributeByKey(UserAttributeKey.FAMILY_NAME));
      context.setVariable("organizationName", 
          user.getAttributeByKey(UserAttributeKey.CUSTOM_ORGANIZATION));
      context.setVariable("stateOfficerPhone", 
          user.getAttributeByKey(UserAttributeKey.PHONE_NUMBER));
    });
  }

  private String applyVariables(Context context, String text) {
    String body = text;

    for (String varString : context.getVariableNames()) {
      body = body.replaceAll("\\$\\{" + varString + "\\}", (String)context.getVariable(varString));
    }

    return body;
  }
}
