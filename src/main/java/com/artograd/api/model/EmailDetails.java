package com.artograd.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailDetails {
  private String recipientEmail;
  private String subject;
  private String body;
  
  /**
   * Constructor.
   * @param recipientEmail email
   * @param subject email
   * @param body email
   */
  public EmailDetails(String recipientEmail, String subject, String body) {
    super();
    this.recipientEmail = recipientEmail;
    this.subject = subject;
    this.body = body;
  }
}