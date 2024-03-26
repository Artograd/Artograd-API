package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Schema
@Getter
@Setter
public class User {

  private List<UserAttribute> attributes;

  public User(List<UserAttribute> attributes) {
    this.attributes = attributes;
  }
}
