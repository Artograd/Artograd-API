package com.artograd.api.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "email_whitelist")
@Schema
@Getter
@Setter
@NoArgsConstructor
public class EmailWhitelistEntry {

  @Id
  @Schema(description = "The unique auto-generated identifier of the tender")
  private String id;

  @Schema(description = "Specific email address")
  private String email;

  @Schema(description = "Email domain (e.g., \"example.com\")")
  private String domain;
}
