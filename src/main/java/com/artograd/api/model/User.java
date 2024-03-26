package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.artograd.api.model.enums.UserAttributeKey;
import com.artograd.api.model.enums.UserRole;

@Schema
@Getter
@Setter
public class User {

	private List<UserAttribute> attributes;

	public User(List<UserAttribute> attributes) {
		this.attributes = attributes;
	}
	
	public UserRole getRole() {
		for (UserAttribute userAttribute : attributes) {
			if (isCognitoGroupAttribute(userAttribute)) {
				return UserRole.fromString(userAttribute.getValue()) ;
			} 
		}
		return UserRole.ANONYMOUS_OR_CITIZEN;
	}

	private boolean isCognitoGroupAttribute(UserAttribute userAttribute) {
		return userAttribute.getEnumKey() != null && userAttribute.getEnumKey().equals(UserAttributeKey.COGNITO_GROUPS);
	}

}
