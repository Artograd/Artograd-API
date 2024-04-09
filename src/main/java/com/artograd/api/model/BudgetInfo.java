package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
public class BudgetInfo {
  @Schema(
      description = "Initial estimation of budget needed for creation and delivery of art object")
  private double initialEstimate;

  @Schema(
      description = "Refined estimation of budget needed for creation and delivery of art object")
  private double currentEstimate;

  @Schema(description = "Amount of money that tender owner requested for fundrising")
  private double fundraisingTarget;

  @Schema(description = "Amount of money that are gathered for art object implementation")
  private double fundraisingGathered;
}
