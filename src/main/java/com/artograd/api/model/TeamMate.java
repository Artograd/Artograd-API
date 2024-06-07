package com.artograd.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "team")
public class TeamMate {
  @Id
  private String id;
  private String name;
  private String role;
  private String profileLink;
  private String pictureURL;
  private int index;
  private boolean active;
}

