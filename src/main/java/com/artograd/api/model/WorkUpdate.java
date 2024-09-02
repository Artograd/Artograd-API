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
public class WorkUpdate {

  @Schema(description = "The unique auto-generated identifier of the work update")
  private String id;

  @Schema(description = "Date of the work update")
  private Date date;

  @Schema(description = "Progress percentage of the work")
  private int progress;

  @Schema(description = "Short summary of the work update")
  private String summary;

  @Schema(description = "Detailed description of the work update")
  private String description;

  @Schema(description = "Files exposing the progress of the work update")
  private List<FileInfo> files;

  @Schema(description = "The ID of the art object that this work update belongs to")
  private String artObjectId;
}
