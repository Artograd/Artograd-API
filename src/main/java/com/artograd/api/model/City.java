package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
public class City {
	
	@Schema(description = "The unique autogenerated ID of the city")
	private String id;

	@Schema(description = "The name of the city")
	private String name;
	
	@Schema(description = "Latitude")
	private double lat;
	
	@Schema(description = "Longtitude")
	private double lng;

}
