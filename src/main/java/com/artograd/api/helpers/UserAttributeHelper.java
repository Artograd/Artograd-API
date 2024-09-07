package com.artograd.api.helpers;

import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;
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
    String givenName = getUserAttributeValue(attributes, "given_name");
    String familyName = getUserAttributeValue(attributes, "family_name");
    return String.format("%s %s", givenName, familyName).trim();
  }

  /**
   * Gets the user attribute value by the provided attribute name.
   *
   * @param user The user.
   * @param attributeName The attribute name.
   * @return The user attribute value.
   */
  public String getUserAttributeValue(User user, String attributeName) {
    return getUserAttributeValue(user.getAttributes(), attributeName);
  }

  /**
   * Gets the user attribute value by the provided attribute name.
   *
   * @param attributes The user attributes.
   * @param attributeName The attribute name.
   * @return The user attribute value.
   */
  public String getUserAttributeValue(List<UserAttribute> attributes, String attributeName) {
    return attributes.stream()
        .filter(attr -> attr.getName().equals(attributeName))
        .findFirst()
        .map(UserAttribute::getValue)
        .orElse("");
  }
}
