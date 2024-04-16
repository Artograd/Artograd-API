package com.artograd.api.model.enums;

import lombok.Getter;

@Getter
public enum UserAttributeKey {
  CUSTOM_FACEBOOK("custom:facebook"),
  CUSTOM_INSTAGRAM("custom:instagram"),
  CUSTOM_LINKEDIN("custom:linkedin"),
  CUSTOM_LOCATION("custom:location"),
  WEBSITE("website"),
  CUSTOM_ORGANIZATION("custom:organization"),
  CUSTOM_JOBTITLE("custom:jobtitle"),
  GIVEN_NAME("given_name"),
  FAMILY_NAME("family_name"),
  PICTURE("picture"),
  COGNITO_USERNAME("cognito:username"),
  CUSTOM_LANG_ISO2("custom:lang_iso2"),
  COGNITO_GROUPS("cognito:groups"),
  EMAIL("email"),
  EMAIL_VERIFIED("email_verified"),
  SHOW_EMAIL("custom:show_email"),
  BANK_ACCOUNT("custom:bank_account"),
  BANK_BENEFICIARY("custom:bank_benefit_bank"),
  BANK_BENEFICIARY_NAME("custom:bank_benefit_name"),
  BANK_IBAN("custom:bank_iban"),
  BANK_SWIFT("custom:bank_swift"),
  BANK_USE_DEFAULT("custom:bank_use_default"),
  PHONE_NUMBER("phone_number"),
  PHONE_NUMBER_VERIFIED("phone_number_verified"),
  SUB("sub");

  private final String attributeKey;

  UserAttributeKey(String attributeKey) {
    this.attributeKey = attributeKey;
  }

  /**
   * Converts a string representation of a UserAttributeKey to its corresponding enum value.
   *
   * @param text the string representation of the UserAttributeKey
   * @return the enum value of the UserAttributeKey, or throws an IllegalArgumentException if
   *         the string does not match any UserAttributeKey
   */
  public static UserAttributeKey fromString(String text) {
    for (UserAttributeKey uak : UserAttributeKey.values()) {
      if (uak.attributeKey.equalsIgnoreCase(text)) {
        return uak;
      }
    }
    throw new IllegalArgumentException("No UserAttributeKey matching " + text);
  }
}
