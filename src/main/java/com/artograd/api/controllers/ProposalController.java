package com.artograd.api.controllers;

import com.artograd.api.model.Proposal;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.IProposalService;
import com.artograd.api.services.ITenderService;
import com.artograd.api.services.IUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenders/{tenderId}/proposals")
public class ProposalController {

  @Autowired private IProposalService proposalService;

  @Autowired private ITenderService tenderService;

  @Autowired private IUserService userService;

  /**
   * Retrieves a specific proposal by its ID.
   *
   * @param tenderId The ID of the tender the proposal belongs to.
   * @param proposalId The ID of the proposal to retrieve.
   * @return ResponseEntity containing the retrieved Proposal object if found, or a ResponseEntity
   *         with a status of HttpStatus.NOT_FOUND if not found.
   */
  @GetMapping("/{proposalId}")
  public ResponseEntity<Proposal> getProposal(
      @PathVariable String tenderId, @PathVariable String proposalId) {
    return proposalService
        .getProposal(tenderId, proposalId)
        .map(proposal -> new ResponseEntity<>(proposal, HttpStatus.OK))
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Creates a proposal for a specific tender.
   *
   * @param tenderId The ID of the tender the proposal belongs to.
   * @param proposal The Proposal object containing the details of the proposal.
   * @param request The HttpServletRequest object containing the request information.
   * @return A ResponseEntity representing the result of the create operation. If the proposal is
   *         successfully created, the ResponseEntity will contain the created Proposal object and a
   *         status of HttpStatus.CREATED. If the operation is not allowed, a ResponseEntity with a
   *         status of HttpStatus.FORBIDDEN and a message of "Operation not allowed" will be
   *         returned. If the proposal could not be created, a ResponseEntity with a status of
   *         HttpStatus.BAD_REQUEST and a message of "Proposal could not be created. Check that you
   *         are not the tender owner" will be returned.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @PostMapping
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<?> createProposal(
      @PathVariable String tenderId, @RequestBody Proposal proposal, HttpServletRequest request) {
    UserTokenClaims claims = userService.getUserTokenClaims(request).orElse(null);
    if (claims == null || tenderService.isTenderOwner(tenderId, claims.getUsername())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Operation not allowed.");
    }

    return proposalService
        .createProposal(tenderId, proposal, claims.getUsername())
        .map(p -> new ResponseEntity<>(p, HttpStatus.CREATED))
        .orElse(
            new ResponseEntity(
                "Proposal could not be created. Check that you are not tender owner.",
                HttpStatus.BAD_REQUEST));
  }

  /**
   * Updates a proposal for a specific tender.
   *
   * @param tenderId The ID of the tender the proposal belongs to.
   * @param proposalId The ID of the proposal to be updated.
   * @param proposal The Proposal object containing the updated details of the proposal.
   * @param request The HttpServletRequest object containing the request information.
   * @return A ResponseEntity representing the result of the update operation. If the update is
   *         successful, the ResponseEntity will have a status of HttpStatus.OK and contain the
   *         updated Proposal object. If the proposal or tender are not found, the ResponseEntity
   *         will have a status of HttpStatus.NOT_FOUND. If the operation is not allowed, the
   *         ResponseEntity will have a status of HttpStatus.FORBIDDEN.
   */
  @PutMapping("/{proposalId}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<?> updateProposal(
      @PathVariable String tenderId,
      @PathVariable String proposalId,
      @RequestBody Proposal proposal,
      HttpServletRequest request) {
    return proposalService.isProposalOperationAllowed(tenderId, proposalId, request)
        ? proposalService
            .updateProposal(tenderId, proposalId, proposal)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build())
        : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }

  /**
   * Deletes a proposal for a specific tender.
   *
   * @param tenderId The ID of the tender the proposal belongs to.
   * @param proposalId The ID of the proposal to be deleted.
   * @param request The HttpServletRequest object containing the request information.
   * @return A ResponseEntity representing the result of the delete operation. If delete is
   *         successful, the ResponseEntity will have a status of HttpStatus.NO_CONTENT. If the
   *         proposal or tender are not found, the ResponseEntity will have a status of
   *         HttpStatus.NOT_FOUND. If the operation is not allowed, the ResponseEntity will have
   *         a status of HttpStatus.FORBIDDEN.
   */
  @DeleteMapping("/{proposalId}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Void> deleteProposal(
      @PathVariable String tenderId, @PathVariable String proposalId, HttpServletRequest request) {
    return proposalService.isProposalOperationAllowed(tenderId, proposalId, request)
        ? proposalService.deleteProposal(tenderId, proposalId)
            ? ResponseEntity.noContent().<Void>build()
            : ResponseEntity.notFound().build()
        : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  } //TODO: This might need refactoring
}
