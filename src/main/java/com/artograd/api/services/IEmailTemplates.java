package com.artograd.api.services;

import com.artograd.api.model.SocialMediaContact;
import com.artograd.api.model.Tender;

public interface IEmailTemplates {
  String getTenderPublicationTemplate(Tender tender, SocialMediaContact contact);
}
