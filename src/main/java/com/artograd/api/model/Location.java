package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class Location {
	
	@Schema(description = "Cascade names of the tender's location, i.e. {name: \"Montenegro\", child: {name: \"Budva\", child: {name: \"Budva\", child: null}}}")
	private NestedLocation nestedLocation;
	
	@Schema(description = "Geopostition of the potential art object")
    private GeoPosition geoPosition;
	
	@Schema(description = "Address line")
    private String addressLine;
	
	@Schema(description = "Address comment")
    private String addressComment;
    
	public Location(NestedLocation nestedLocation, GeoPosition geoPosition, String addressLine, String addressComment) {
		super();
		this.nestedLocation = nestedLocation;
		this.geoPosition = geoPosition;
		this.addressLine = addressLine;
		this.addressComment = addressComment;
	}

	public NestedLocation getNestedLocation() {
		return nestedLocation;
	}
	public void setNestedLocation(NestedLocation nestedLocation) {
		this.nestedLocation = nestedLocation;
	}
	public GeoPosition getGeoPosition() {
		return geoPosition;
	}
	public void setGeoPosition(GeoPosition geoPosition) {
		this.geoPosition = geoPosition;
	}
	public String getAddressLine() {
		return addressLine;
	}
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
	public String getAddressComment() {
		return addressComment;
	}
	public void setAddressComment(String addressComment) {
		this.addressComment = addressComment;
	}
}
