package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
public class NestedLocation {
	
	@Schema(description = "Id of location entity")
	private String id;
	
	@Schema(description = "name of location entity")
	private String name;
	
	@Schema(description = "nested child")
	private NestedLocation child;
	
	public NestedLocation(String id, String name, NestedLocation child) {
		this.id = id;
		this.name = name;
		this.child = child;
	}

}
