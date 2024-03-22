package com.artograd.api.controllers;

import com.artograd.api.model.Proposal;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.CognitoService;
import com.artograd.api.services.ProposalService;
import com.artograd.api.services.TenderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tenders/{tenderId}/proposals")
public class ProposalController {
	
    @Autowired
    private ProposalService proposalService;
    
    @Autowired
    private TenderService tenderService;

    @Autowired
    private CognitoService cognitoService;
    
    @GetMapping("/{proposalId}")
    public ResponseEntity<Proposal> getProposal(@PathVariable String tenderId, @PathVariable String proposalId) {
        return proposalService.getProposal(tenderId, proposalId)
            .map(proposal -> new ResponseEntity<>(proposal, HttpStatus.OK))
            .orElse(ResponseEntity.notFound().build());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createProposal(@PathVariable String tenderId, @RequestBody Proposal proposal, HttpServletRequest request) {
    	UserTokenClaims claims = cognitoService.getUserTokenClaims(request).orElse(null);
    	if (claims == null || tenderService.isTenderOwner(tenderId, claims.getUsername())) {
    		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Operation not allowed.");
    	}

    	proposal.setOwnerId(claims.getUsername());
    	return proposalService.createProposal(tenderId, proposal)
            .map(p -> new ResponseEntity<>(p, HttpStatus.CREATED))
            .orElse( new ResponseEntity("Proposal could not be created. Check that you are not tender owner.", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{proposalId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateProposal(@PathVariable String tenderId, @PathVariable String proposalId, @RequestBody Proposal proposal, HttpServletRequest request) {
        return isProposalOperationAllowed(tenderId, proposalId, request)
            ? proposalService.updateProposal(tenderId, proposalId, proposal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build())
            : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/{proposalId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteProposal(@PathVariable String tenderId, @PathVariable String proposalId, HttpServletRequest request) {
        return isProposalOperationAllowed(tenderId, proposalId, request)
            ? proposalService.deleteProposal(tenderId, proposalId)
                ? ResponseEntity.noContent().<Void>build()
                : ResponseEntity.notFound().build()
            : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    private boolean isProposalOperationAllowed(String tenderId, String proposalId, HttpServletRequest request) {
        return proposalService.getProposal(tenderId, proposalId)
            .map(proposal -> cognitoService.getUserTokenClaims(request)
                .map(claims -> claims.getUsername() != null && claims.getUsername().equals(proposal.getOwnerId()))
                .orElse(false))
            .orElse(false);
    }
}

