package com.artograd.api.model.enums;

public enum UserAttributeKey {
    CUSTOM_FACEBOOK("custom:facebook"),
    CUSTOM_INSTAGRAM("custom:instagram"),
    CUSTOM_LINKEDIN("custom:linkedin"),
    CUSTOM_LOCATION("custom:location"),
    CUSTOM_ORGANIZATION("custom:organization"),
    CUSTOM_JOBTITLE("custom:jobtitle"),
    GIVEN_NAME("given_name"),
    FAMILY_NAME("family_name"),
    PICTURE("picture"),
    COGNITO_USERNAME("cognito:username"),
    CUSTOM_LANG_ISO2("custom:lang_iso2"),
    COGNITO_GROUPS("cognito:groups"),
    EMAIL("email"),
    PHONE_NUMBER("phone_number");

    private final String attributeKey;

    UserAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public static UserAttributeKey fromString(String text) {
        for (UserAttributeKey b : UserAttributeKey.values()) {
            if (b.attributeKey.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
