package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class NestedLocation {
	
	@Schema(description = "Id of location entity")
	private String id;
	
	@Schema(description = "name of location entity")
	private String name;
	
	@Schema(description = "nested child")
	private NestedLocation child;
	
	public NestedLocation(String id, String name, NestedLocation child) {
		super();
		this.id = id;
		this.name = name;
		this.child = child;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public NestedLocation getChild() {
		return child;
	}
	public void setChild(NestedLocation child) {
		this.child = child;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
