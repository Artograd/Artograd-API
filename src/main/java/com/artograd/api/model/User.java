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
			if ( userAttribute.getEnumKey() != null && userAttribute.getEnumKey().equals( UserAttributeKey.COGNITO_GROUPS ) ) {
				return UserRole.fromString( userAttribute.getValue() ) ;
			} 
		}
		return null;
	}

}
