package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
public class ExpenseReport {

  @Schema(description = "The unique auto-generated identifier of the expense report")
  private String id;

  @Schema(description = "Date of the expense report")
  private Date date;

  @Schema(description = "Amount of the expense report")
  private double amount;

  @Schema(description = "Short summary of the expense report")
  private String summary;

  @Schema(description = "Detailed description of the expense report")
  private String description;

  @Schema(description = "Files related to the expense report")
  private List<FileInfo> files;

  @Schema(description = "The ID of the art object that this expense report belongs to")
  private String artObjectId;
}
