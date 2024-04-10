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
   * Creates a tender.
   *
   * @param tender The tender object to be created
   * @param request The HttpServletRequest object
   * @return Returns a ResponseEntity with the created tender if the user is authorized and
   *         the creation is successful. Otherwise, returns a ResponseEntity with status
   *         HttpStatus.FORBIDDEN
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
   * Retrieve a tender by its ID.
   *
   * @param id The unique identifier of the tender.
   * @return Returns a ResponseEntity with the tender if found, or a ResponseEntity with status
   *         HttpStatus.NOT_FOUND if the tender is not found.
   */
  @GetMapping("/{id}")
  public ResponseEntity<Tender> getTender(@PathVariable String id) {
    return tenderService
        .getTender(id)
        .map(tender -> ResponseEntity.ok().body(tender))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Updates a tender by its ID.
   *
   * @param id The unique identifier of the tender to update.
   * @param tender The updated tender object.
   * @param request The HttpServletRequest object.
   * @return Returns a ResponseEntity with the updated tender if the user is authorized and
   *         the update is successful. Otherwise, returns a ResponseEntity with status
   *         HttpStatus.FORBIDDEN if the user is not authorized and HttpStatus.NOT_FOUND if
   *         the tender is not found.
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
   * Deletes a tender by its ID.
   *
   * @param id      The unique identifier of the tender to delete.
   * @param request The HttpServletRequest object.
   * @return Returns a ResponseEntity with no content if the user is authorized and the deletion is
   *         successful. Otherwise, returns a ResponseEntity with status HttpStatus.FORBIDDEN if
   *         the user is not authorized.
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
   * Retrieves a list of tenders based on the given search criteria.
   *
   * @param criteria The criteria used to search for tenders.
   * @return Returns a ResponseEntity with a list of tenders if successful.
   */
  @GetMapping
  public ResponseEntity<List<Tender>> searchTenders(@ModelAttribute TenderSearchCriteria criteria) {

    List<Tender> tenders = tenderService.searchTenders(criteria);
    return ResponseEntity.ok().body(tenders);
  }

  /**
   * Retrieves the count of tenders based on the owner ID and statuses.
   *
   * @param ownerId  The unique identifier of the owner.
   * @param statuses The list of statuses to filter the tenders.
   * @return A ResponseEntity with the count of tenders.
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
