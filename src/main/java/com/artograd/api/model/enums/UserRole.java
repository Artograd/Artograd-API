package com.artograd.api.model.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum UserRole {
  ANONYMOUS_OR_CITIZEN("AnonymousOrCitizen"),
  ARTIST("Artists"),
  OFFICIAL("Officials");

  private final String roleName;
  private static final Map<String, UserRole> ROLE_MAP = new HashMap<>();

  static {
    for (UserRole role : UserRole.values()) {
      ROLE_MAP.put(role.roleName.toLowerCase(), role);
    }
  }

  UserRole(String roleName) {
    this.roleName = roleName;
  }

  public static UserRole fromString(String text) {
    return text != null ? ROLE_MAP.getOrDefault(text.toLowerCase(), ANONYMOUS_OR_CITIZEN) :
        ANONYMOUS_OR_CITIZEN;
  }

  @Override
  public String toString() {
    return roleName;
  }
}
