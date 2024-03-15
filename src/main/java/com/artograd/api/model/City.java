package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class City {
	
	@Schema(description = "The unique autogenerated ID of the city")
	private String id;

	@Schema(description = "The name of the city")
	private String name;
	
	@Schema(description = "Latitude")
	private double lat;
	
	@Schema(description = "Longtitude")
	private double lng;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
}
