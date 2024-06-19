package com.artograd.api.model;

import com.artograd.api.model.enums.UserAttributeKey;
import com.artograd.api.model.enums.UserRole;
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

  /**
   * Retrieves the role of the user. It searches for the user attribute that represents
   * the cognito group and returns the corresponding UserRole value. If no cognito group
   * attribute is found, it returns the default role ANONYMOUS_OR_CITIZEN.
   *
   * @return the UserRole value representing the role of the user
   */
  public UserRole getRole() {
    for (UserAttribute userAttribute : attributes) {
      if (isCognitoGroupAttribute(userAttribute)) {
        return UserRole.fromString(userAttribute.getValue());
      }
    }
    return UserRole.ANONYMOUS_OR_CITIZEN;
  }

  private boolean isCognitoGroupAttribute(UserAttribute userAttribute) {
    return userAttribute.getEnumKey() != null
        && userAttribute.getEnumKey().equals(UserAttributeKey.COGNITO_GROUPS);
  }
  
  /**
   * Retrieves the attribute value by the specified enum key.
   *
   * @param key the UserAttributeKey enum value
   * @return the value of the attribute if found, otherwise null
   */
  public String getAttributeByKey(UserAttributeKey key) {
    for (UserAttribute userAttribute : attributes) {
      if (userAttribute.getEnumKey() == key) {
        return userAttribute.getValue();
      }
    }
    return null;
  }
}
