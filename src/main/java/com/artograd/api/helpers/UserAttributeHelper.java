package com.artograd.api.helpers;

import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;
import com.artograd.api.model.enums.UserAttributeKey;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UserAttributeHelper {

  /**
   * Formats the user name.
   *
   * @param user The user.
   * @return The formatted user name.
   */
  public String formatUserName(User user) {
    return formatUserName(user.getAttributes());
  }

  /**
   * Formats the user name.
   *
   * @param attributes The user attributes.
   * @return The formatted user name.
   */
  public String formatUserName(List<UserAttribute> attributes) {
    String givenName = getUserAttributeValue(attributes, UserAttributeKey.GIVEN_NAME);
    String familyName = getUserAttributeValue(attributes, UserAttributeKey.FAMILY_NAME);
    return String.format("%s %s", givenName, familyName).trim();
  }

  /**
   * Gets the user attribute value by the provided attribute name.
   *
   * @param user The user.
   * @param attribute The attribute name.
   * @return The user attribute value.
   */
  public String getUserAttributeValue(User user, UserAttributeKey attribute) {
    return getUserAttributeValue(user.getAttributes(), attribute);
  }

  /**
   * Gets the user attribute value by the provided attribute name.
   *
   * @param attributes The user attributes.
   * @param attribute The attribute name.
   * @return The user attribute value.
   */
  public String getUserAttributeValue(List<UserAttribute> attributes, UserAttributeKey attribute) {
    return attributes.stream()
        .filter(attr -> attr.getName().equals(attribute.getAttributeKey()))
        .findFirst()
        .map(UserAttribute::getValue)
        .orElse("");
  }
}
