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

  /**
   * Converts a string representation of a UserRole to its corresponding enum value.
   *
   * @param text the string representation of the UserRole
   * @return the enum value of the UserRole, or ANONYMOUS_OR_CITIZEN if the string does not match
   *         any UserRole
   */
  public static UserRole fromString(String text) {
    for (UserRole role : UserRole.values()) {
      if (role.roleName.equalsIgnoreCase(text)) {
        return role;
      }
    }
    return ANONYMOUS_OR_CITIZEN;
  }
}
