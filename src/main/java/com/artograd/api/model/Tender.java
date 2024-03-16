package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema
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
    @Schema(description = "The user email of the official who created the tender")
    private String ownerEmail;
    @Schema(description = "Wheather email should be sent in the object as response")
    private boolean showEmail;
    @Schema(description = "links to files of the supported documents")
    private List<String> files;
    @Schema(description = "Status of the tender")
    private String status;
    @Schema(description = "The user name of the official who created the tender")
    private String ownerName;
    @Schema(description = "The user id of the official who created the tender")
    private String ownerId;
    @Schema(description = "The name of the organization of the official who created the tender")
    private String organization;
    @Schema(description = "the link to owner's picture")
    private String ownerPicture;
    @Schema(description = "Tender creation date")
    private Date createdAt;
    @Schema(description = "Tender last modification date")
    private Date modifiedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getSubmissionStart() {
        return submissionStart;
    }

    public void setSubmissionStart(Date submissionStart) {
        this.submissionStart = submissionStart;
    }

    public Date getSubmissionEnd() {
        return submissionEnd;
    }

    public void setSubmissionEnd(Date submissionEnd) {
        this.submissionEnd = submissionEnd;
    }

    public Date getExpectedDelivery() {
        return expectedDelivery;
    }

    public void setExpectedDelivery(Date expectedDelivery) {
        this.expectedDelivery = expectedDelivery;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public boolean isShowEmail() {
        return showEmail;
    }

    public void setShowEmail(boolean showEmail) {
        this.showEmail = showEmail;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getOwnerPicture() {
        return ownerPicture;
    }

    public void setOwnerPicture(String ownerPicture) {
        this.ownerPicture = ownerPicture;
    }
}
