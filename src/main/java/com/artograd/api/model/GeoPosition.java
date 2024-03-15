package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class GeoPosition {
	
	@Schema(description = "Latitude of the potential art object")
    private double latitude;
	
	@Schema(description = "Longitude of the potential art object")
    private double longitude;

	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}