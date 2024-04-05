package com.artograd.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
public class PaymentInfo {
	
	@Schema(description = "Short unique 6-digit number per each art object")
	private String articul;
	
	@Schema(description = "The name of person or organization to receive the income")
	private String beneficiaryName;
	
	@Schema(description = "The bank name")
	private String beneficiaryBank;
	
	@Schema(description = "The number of account")
	private String accountNumber;
	
	@Schema(description = "International bank account number")
	private String iban;
	
	@Schema(description = "International payment code")
	private String swift;
}
