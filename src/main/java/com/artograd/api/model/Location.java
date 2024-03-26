package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
public class Location {

  @Schema(
      description =
          "Cascade names of the tender's location, i.e. {name: \"Montenegro\", child: {name: \"Budva\", child: {name: \"Budva\", child: null}}}")
  private NestedLocation nestedLocation;

  @Schema(description = "Geopostition of the potential art object")
  private GeoPosition geoPosition;

  @Schema(description = "Address line")
  private String addressLine;

  @Schema(description = "Address comment")
  private String addressComment;

  public Location(
      NestedLocation nestedLocation,
      GeoPosition geoPosition,
      String addressLine,
      String addressComment) {
    this.nestedLocation = nestedLocation;
    this.geoPosition = geoPosition;
    this.addressLine = addressLine;
    this.addressComment = addressComment;
  }
}
