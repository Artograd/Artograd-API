package com.artograd.api.controllers;

import com.artograd.api.model.Proposal;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.IProposalService;
import com.artograd.api.services.ITenderService;
import com.artograd.api.services.IUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
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

@AllArgsConstructor
@RestController
@RequestMapping("/tenders/{tenderId}/proposals")
public class ProposalController {

  private IProposalService proposalService;
  private ITenderService tenderService;
  private IUserService userService;

  /**
   * Retrieves a specific proposal by its tender ID and proposal ID.
   *
   * @param tenderId the ID of the tender associated with the proposal
   * @param proposalId the ID of the proposal to retrieve
   * @return a ResponseEntity containing the requested proposal, if found with a status code of 200
   *     (OK), otherwise returns ResponseEntity with a status code of 404 (Not Found)
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
   * Creates a proposal for a given tender.
   *
   * @param tenderId the ID of the tender associated with the proposal
   * @param proposal the proposal to be created
   * @param request the HttpServletRequest object
   * @return a ResponseEntity representing the result of the operation
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
   * Updates an existing proposal for a given tender.
   *
   * @param tenderId the ID of the tender associated with the proposal
   * @param proposalId the ID of the proposal to be updated
   * @param proposal the updated proposal object
   * @param request the HttpServletRequest object
   * @return a ResponseEntity representing the result of the operation. If the operation is allowed
   *     and the proposal is successfully updated, returns a ResponseEntity with a status code of
   *     200 (OK) and the updated proposal. If the operation is not allowed, returns a
   *     ResponseEntity with a status code of 403 (FORBIDDEN). If the proposal or the tender cannot
   *     be found, returns a ResponseEntity with a status code of 404 (NOT FOUND).
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
   * Deletes a proposal for a given tender.
   *
   * @param tenderId the ID of the tender associated with the proposal
   * @param proposalId the ID of the proposal to be deleted
   * @param request the HttpServletRequest object
   * @return a ResponseEntity with a status code indicating the result of the operation -
   *     ResponseEntity.noContent() with a status code of 204 (No Content) if the proposal is
   *     deleted successfully, - ResponseEntity.notFound() with a status code of 404 (Not Found) if
   *     the proposal or the tender cannot be found, - ResponseEntity.status(HttpStatus.FORBIDDEN)
   *     with a status code of 403 (Forbidden) if the operation is not allowed
   *     TODO: It might need refactoring
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
  }

  /**
   * Like a proposal.
   *
   * @param tenderId the ID of the tender associated with the proposal
   * @param proposalId the ID of the proposal to be liked
   * @param request the HttpServletRequest object
   * @return a ResponseEntity representing the result of the operation. If the user is unauthorized,
   *     returns ResponseEntity with a status code of 401 (Unauthorized). If the proposal is liked
   *     successfully, returns ResponseEntity with a status code of 200 (OK) and the updated
   *     proposal. If the proposal or the tender cannot be found, returns ResponseEntity with a
   *     status code of 404 (Not Found).
   */
  @PostMapping("/{proposalId}/like")
  public ResponseEntity<?> likeProposal(
      @PathVariable String tenderId, @PathVariable String proposalId, HttpServletRequest request) {
    UserTokenClaims claims = userService.getUserTokenClaims(request).orElse(null);
    if (claims == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    return proposalService
        .likeProposal(tenderId, proposalId, claims.getUsername())
        .map(proposal -> ResponseEntity.ok().body(proposal))
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Unlike a proposal for a given tender.
   *
   * @param tenderId the ID of the tender associated with the proposal
   * @param proposalId the ID of the proposal to be unliked
   * @param request the HttpServletRequest object
   * @return a ResponseEntity representing the result of the operation. If the user is unauthorized,
   *     returns ResponseEntity with a status code of 401 (Unauthorized). If the proposal is unliked
   *     successfully, returns ResponseEntity with a status code of 200 (OK) and the updated
   *     proposal. If the proposal or the tender cannot be found, returns ResponseEntity with a
   *     status code of 404 (Not Found).
   */
  @PostMapping("/{proposalId}/unlike")
  public ResponseEntity<?> unlikeProposal(
      @PathVariable String tenderId, @PathVariable String proposalId, HttpServletRequest request) {
    UserTokenClaims claims = userService.getUserTokenClaims(request).orElse(null);
    if (claims == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    return proposalService
        .unlikeProposal(tenderId, proposalId, claims.getUsername())
        .map(proposal -> ResponseEntity.ok().body(proposal))
        .orElse(ResponseEntity.notFound().build());
  }
}
