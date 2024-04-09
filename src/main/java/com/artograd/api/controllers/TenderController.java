package com.artograd.api.controllers;

import com.artograd.api.model.Tender;
import com.artograd.api.model.TenderSearchCriteria;
import com.artograd.api.services.ITenderService;
import com.artograd.api.services.IUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenders")
public class TenderController {

  @Autowired private ITenderService tenderService;

  @Autowired private IUserService userService;

  /**
   * Creates a new Tender.
   *
   * @param tender the Tender object to be created
   * @param request the HttpServletRequest object
   * @return the ResponseEntity with the created Tender object or a forbidden response if the user
   *         does not have permission
   */
  @PostMapping
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Tender> createTender(
      @RequestBody Tender tender, HttpServletRequest request) {
    return userService
        .getUserTokenClaims(request)
        .filter(claims -> claims.getUsername() != null)
        .filter(claims -> claims.getUsername().equals(tender.getOwnerId()) && claims.isOfficer())
        .map(claims -> tenderService.createTender(tender))
        .map(createdTender -> ResponseEntity.status(HttpStatus.CREATED).body(createdTender))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
  }

  /**
   * Retrieves a tender with the specified ID.
   *
   * @param id the ID of the tender to retrieve
   * @return the ResponseEntity with the retrieved Tender object if it exists, or a not found
   *         response if it does not
   */
  @GetMapping("/{id}")
  public ResponseEntity<Tender> getTender(@PathVariable String id) {
    return tenderService
        .getTender(id)
        .map(tender -> ResponseEntity.ok().body(tender))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Updates a tender with the specified ID.
   *
   * @param id the ID of the tender to update
   * @param tender the updated Tender object
   * @param request the HttpServletRequest object
   * @return the ResponseEntity with the updated Tender object if successful, or a forbidden
   *         response if the user does not have permission, or a not found response if the tender
   *         does not exist
   */
  @PutMapping("/{id}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Tender> updateTender(
      @PathVariable String id, @RequestBody Tender tender, HttpServletRequest request) {
    return isDenied(id, request)
        ? ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        : tenderService
            .updateTender(tender)
            .map(updatedTender -> ResponseEntity.ok().body(updatedTender))
            .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Deletes a tender with the specified ID.
   *
   * @param id      The ID of the tender to delete.
   * @param request The HttpServletRequest object.
   * @return The ResponseEntity with no content if the tender is successfully deleted, or a
   *         forbidden response if the user does not have permission.
   */
  @DeleteMapping("/{id}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Void> deleteTender(@PathVariable String id, HttpServletRequest request) {
    if (isDenied(id, request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    tenderService.deleteTender(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Retrieves a list of tenders based on the search criteria.
   *
   * @param criteria the TenderSearchCriteria object containing the search criteria
   * @return the ResponseEntity with the list of retrieved Tender objects
   */
  @GetMapping
  public ResponseEntity<List<Tender>> searchTenders(@ModelAttribute TenderSearchCriteria criteria) {

    List<Tender> tenders = tenderService.searchTenders(criteria);
    return ResponseEntity.ok().body(tenders);
  }

  /**
   * Retrieves the count of tenders owned by a specific owner and with specified statuses.
   *
   * @param ownerId  the ID of the owner
   * @param statuses optional list of statuses to filter by
   * @return the ResponseEntity with the count of tenders as a Long if successful
   */
  @GetMapping("/count/{ownerId}")
  public ResponseEntity<Long> getTenderCountByOwnerIdAndStatuses(
      @PathVariable String ownerId, @RequestParam(required = false) List<String> statuses) {
    long count = tenderService.getCountByOwnerIdAndStatusIn(ownerId, statuses);
    return ResponseEntity.ok(count);
  }

  private boolean isDenied(String tenderId, HttpServletRequest request) {
    return userService
        .getUserTokenClaims(request)
        .map(
            claims ->
                !claims.isOfficer() || !tenderService.isTenderOwner(tenderId, claims.getUsername()))
        .orElse(true);
  }
}
