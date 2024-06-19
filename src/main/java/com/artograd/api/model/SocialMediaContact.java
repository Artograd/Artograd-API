package com.artograd.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "contacts")
public class SocialMediaContact {

  @Id
  private String id;
  private String userId;
  private String contactName;
  private String contactMassMediaName;
  private String contactEmail;
  private String contactLanguage;
  private String phone;
  private boolean active;
}
