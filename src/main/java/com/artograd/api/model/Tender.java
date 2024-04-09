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
public class Tender {
	
	@Schema(description = "The unique auto-generated identifier of the tender")
	private String id;
	
	@Schema(description = "The title of the tender")
	private String title;
	
	@Schema(description = "The description of the tender")
    private String description;
	
	@Schema(description = "The date when proposal submission starts")
    private Date submissionStart;
	
	@Schema(description = "The date when proposal submission ends")
    private Date submissionEnd;
	
	@Schema(description = "The date when its expected to the art object from this tender to be ready")
    private Date expectedDelivery;
	
	@Schema(description = "The list of categories of the tender")
    private List<String> category;
	
	@Schema(description = "The location info of the tender")
    private Location location;
	
	@Schema(description = "The ID of the leaf object in the location nested hierarcy.")
	private String locationLeafId;
	
	@Schema(description = "The user email of the official who created the tender")
    private String ownerEmail;
	
	@Schema(description = "Wheather email should be sent in the object as response")
    private boolean showEmail;
	
	@Schema(description = "links to files of the supported documents")
    private List<String> files;
	
	@Schema(description = "links to small files of the supported documents")
    private List<String> snapFiles;
	
	@Schema(description = "Status of the tender")
    private String status;
	
	@Schema(description = "The user name of the official who created the tender")
    private String ownerName;
	
	@Schema(description = "The user id of the official who created the tender")
    private String ownerId;
	
	@Schema(description = "The name of the organization of the official who created the tender")
    private String organization;
	
	@Schema(description = "The link to owner's picture")
    private String ownerPicture;
	
	@Schema(description = "Tender creation date")
    private Date createdAt;
	
	@Schema(description = "Tender last modification date")
    private Date modifiedAt;
	
	@Schema(description = "The proposals for the tender")
	private List<Proposal> proposals;
	
}
