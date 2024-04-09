package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {
  private String path;
  private String snapPath;
  private String name;
  private long size;
  private int id;

  @Schema(description = "Type of file: image, iframe (for pdf), attachment")
  private String type;

  private String extension;
}
