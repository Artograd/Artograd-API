package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
public class FileInfo {
  private String path;
  private String snapPath;
  private String name;
  private long size;
  private int id;

  @Schema(description = "Type of file: image, iframe (for pdf), attachment")
  private String type;

  private String extension;

  public FileInfo(
      String path, String snapPath, String name, long size, int id, String type, String extension) {
    this.path = path;
    this.snapPath = snapPath;
    this.name = name;
    this.size = size;
    this.id = id;
    this.type = type;
    this.extension = extension;
  }
}
