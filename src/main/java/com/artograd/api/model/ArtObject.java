package com.artograd.api.model;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
public class ArtObject {
	
	@Schema(description = "Art object title")
	private String title;
	
	@Schema(description = "Art object description")
	private String description;
	
	@Schema(description = "Renderings of winner's idea")
	private FileInfo files;
	
	@Schema(description = "Reference to tender")
	private Tender tender;
	
	@Schema(description = "Art object budget")
	private BudgetInfo budget;
	
	@Schema(description = "Art object Status")
	private String status;
	
	@Schema(description = "Art object categories")
	private List<String> category;
	
	@Schema(description = "Art object location")
	private Location location;
	
	@Schema(description = "Art object delivery date")
	private Date deliveryDate;
	
	@Schema(description = "Art object owner")
	private UserInfo owner;	
	
	@Schema(description = "Tender winner who became art object supplier")
	private UserInfo supplier;
	
	@Schema(description = "Art object fundraising info for payment")
	private PaymentInfo payment;
}
