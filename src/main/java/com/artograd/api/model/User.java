package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema
public class User {
	private List<UserAttribute> attributes;

	public User(List<UserAttribute> attributes) {
		super();
		this.attributes = attributes;
	}

	public List<UserAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<UserAttribute> attributes) {
		this.attributes = attributes;
	}
}
