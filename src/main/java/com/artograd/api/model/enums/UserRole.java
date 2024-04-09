package com.artograd.api.model.enums;

import lombok.Getter;

@Getter
public enum UserRole {
  ANONYMOUS_OR_CITIZEN("AnonymousOrCitizen"),
  ARTIST("Artists"),
  OFFICIAL("Officials");

  private final String roleName;

  UserRole(String roleName) {
    this.roleName = roleName;
  }

  public static UserRole fromString(String text) {
    for (UserRole role : UserRole.values()) {
      if (role.roleName.equalsIgnoreCase(text)) {
        return role;
      }
    }
    return ANONYMOUS_OR_CITIZEN;
  }
}
