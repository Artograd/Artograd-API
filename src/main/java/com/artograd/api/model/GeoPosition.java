package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
public class GeoPosition {

  @Schema(description = "Latitude of the potential art object")
  private double latitude;

  @Schema(description = "Longitude of the potential art object")
  private double longitude;
}
