package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class UserAttribute {
	
	private String name;
	private String value;
	
	public UserAttribute(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
