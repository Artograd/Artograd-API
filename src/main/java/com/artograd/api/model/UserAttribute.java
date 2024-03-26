package com.artograd.api.model;

import com.artograd.api.model.enums.UserAttributeKey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
public class UserAttribute {
	
	private String name;
	
	private UserAttributeKey enumKey;

	private String value;
	
	public UserAttribute(String name, String value) {
		this.enumKey = UserAttributeKey.fromString(name);
		this.name = name;
		this.value = value;
	}
}
