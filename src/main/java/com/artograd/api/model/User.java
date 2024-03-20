package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema
@Getter
@Setter
public class User {

	private List<UserAttribute> attributes;

	public User(List<UserAttribute> attributes) {
		this.attributes = attributes;
	}

}
