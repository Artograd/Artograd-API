package com.artograd.api.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
public class Proposal {
	
	@Id
	@Schema(description = "The unique auto-generated identifier of the proposal")
	private String id;
	
	@Schema(description = "The title of the proposal idea")
	private String title;
	
	@Schema(description = "The description of proposal idea")
	private String description;
	
	@Schema(description = "The list of attached images")
	private FileInfo images;
	
	@Schema(description = "The cover image")
	private FileInfo cover;
	
	@Schema(description = "Estimated amount of days required for implementation of proposal")
	private int estimatedDuration;
	
	@Schema(description = "Estimated amount of money required for implementation of proposal. Should be seen to tender owner only.")
	private double estimatedCost;
	
	@Schema(description = "The username attribute of a user")
	private String ownerId;
	
	@Schema(description = "The name and surname of the owner")
	private String ownerName;
	
	@Schema(description = "The link to owner's picture")
	private String ownerPicture;
	
	@Schema(description = "The name of organization of the owner")
	private String ownerOrg;
	
	@Schema(description = "Proposal creation date")
	private Date createdAt;
	
	@Schema(description = "Proposal last modification date")
	private Date modifiedAt;
	
}
