package com.artograd.api.helpers;

import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UserAttributeHelper {

  public String formatUserName(User user) {
    return formatUserName(user.getAttributes());
  }

  public String formatUserName(List<UserAttribute> attributes) {
    String givenName = getUserAttributeValue(attributes, "given_name");
    String familyName = getUserAttributeValue(attributes, "family_name");
    return String.format("%s %s", givenName, familyName).trim();
  }

  public String getUserAttributeValue(User user, String attributeName) {
    return getUserAttributeValue(user.getAttributes(), attributeName);
  }

  public String getUserAttributeValue(List<UserAttribute> attributes, String attributeName) {
    return attributes.stream()
        .filter(attr -> attr.getName().equals(attributeName))
        .findFirst()
        .map(UserAttribute::getValue)
        .orElse("");
  }
}
