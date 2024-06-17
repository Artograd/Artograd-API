package com.artograd.api.services;

import com.artograd.api.model.Tender;

public interface IEmailService {
  void sendEmailsTenderIsPublished(Tender tender);
}