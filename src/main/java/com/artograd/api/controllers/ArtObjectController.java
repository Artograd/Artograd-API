package com.artograd.api.controllers;

import com.artograd.api.model.ArtObject;
import com.artograd.api.model.ArtObjectSearchCriteria;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.IArtObjectService;
import com.artograd.api.services.ITenderService;
import com.artograd.api.services.IUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/artobjects")
@AllArgsConstructor
public class ArtObjectController {

  private IArtObjectService artObjectService;
  private IUserService userService;
  private ITenderService tenderService;

  /**
   * Creates an art object based on the given tender ID and winner proposal ID.
   *
   * @param tenderId The ID of the tender for which the art object is being created
   * @param winnerProposalId The ID of the winner proposal
   * @param request The HTTP request
   * @return A ResponseEntity representing the status of the creation operation - HttpStatus.CREATED
   *     if the art object is successfully created - HttpStatus.FORBIDDEN if the user does not have
   *     permission to create the art object
   */
  @PostMapping
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<?> createArtObject(
      @RequestParam String tenderId,
      @RequestParam String winnerProposalId,
      HttpServletRequest request) {
    Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);

    return claims
        .filter(UserTokenClaims::isOfficer)
        .flatMap(c -> tenderService.getTender(tenderId))
        .filter(tender -> tender.getOwnerId().equals(claims.get().getUsername()))
        .flatMap(tender -> artObjectService.createArtObject(tenderId, winnerProposalId))
        .map(artObject -> ResponseEntity.status(HttpStatus.CREATED).body(artObject))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
  }

  /**
   * Retrieves the art object with the specified ID.
   *
   * @param id The ID of the art object to retrieve
   * @return A ResponseEntity object representing the status of the retrieval operation: -
   *     ResponseEntity.ok() with the body set to the retrieved art object if the object exists -
   *     ResponseEntity.notFound() if the object does not exist
   */
  @GetMapping("/{id}")
  public ResponseEntity<ArtObject> getArtObject(@PathVariable String id) {
    return artObjectService
        .getArtObject(id)
        .map(artObject -> ResponseEntity.ok().body(artObject))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Updates an art object with the specified ID.
   *
   * @param id The ID of the art object to update
   * @param artObject The updated art object
   * @param request The HTTP request
   * @return A ResponseEntity representing the status of the update operation: - ResponseEntity.ok()
   *     with the body set to the updated art object if the update is successful -
   *     ResponseEntity.status(HttpStatus.FORBIDDEN) if the user does not have permission to update
   *     the art object
   */
  @PutMapping("/{id}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<?> updateArtObject(
      @PathVariable String id, @RequestBody ArtObject artObject, HttpServletRequest request) {
    Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);

    return claims
        .filter(UserTokenClaims::isOfficer)
        .flatMap(c -> tenderService.getTender(artObject.getTender().getId()))
        .filter(tender -> tender.getOwnerId().equals(claims.get().getUsername()))
        .flatMap(tender -> artObjectService.updateArtObject(id, artObject))
        .map(updatedArtObject -> ResponseEntity.ok().body(updatedArtObject))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
  }

  /**
   * Partially updates an art object with the specified ID.
   *
   * @param id The ID of the art object to patch
   * @param artObject The patched art object
   * @param request The HTTP request
   * @return A ResponseEntity representing the status of the update operation: - ResponseEntity.ok()
   *     with the body set to the patched art object if the patch is successful -
   *     ResponseEntity.status(HttpStatus.FORBIDDEN) if the user does not have permission to modify
   *     the art object
   */
  @PatchMapping("/{id}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<?> patchArtObject(
      @PathVariable String id, @RequestBody ArtObject artObject, HttpServletRequest request) {
    Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);
    String username = claims.get().getUsername();
    
    return claims
        .filter(UserTokenClaims::isOfficer)
        .flatMap(c -> artObjectService.getArtObject(id))
        .filter(ao -> 
             ao.getOwner().getId().equals(username) || ao.getSupplier().getId().equals(username))
        .flatMap(tender -> artObjectService.patchArtObject(id, artObject))
        .map(patchedArtObject -> ResponseEntity.ok().body(patchedArtObject))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
  }

  /**
   * Deletes an art object with the specified ID.
   *
   * @param id The ID of the art object to delete
   * @param request The HttpServletRequest object
   * @return A ResponseEntity representing the status of the delete operation: -
   *     ResponseEntity.noContent() if the art object is successfully deleted -
   *     ResponseEntity.status(HttpStatus.FORBIDDEN) if the user does not have permission to delete
   *     the art object
   */
  @DeleteMapping("/{id}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Void> deleteArtObject(@PathVariable String id, HttpServletRequest request) {
    Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);

    return claims
        .filter(UserTokenClaims::isOfficer)
        .flatMap(c -> artObjectService.getArtObject(id))
        .filter(
            artObject ->
                tenderService
                    .getTender(artObject.getTender().getId())
                    .get()
                    .getOwnerId()
                    .equals(claims.get().getUsername()))
        .map(
            artObject -> {
              artObjectService.deleteArtObject(id);
              return ResponseEntity.noContent().<Void>build();
            })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
  }

  /**
   * Performs a search for art objects based on the given criteria.
   *
   * @param criteria The criteria used for searching art objects
   * @return A ResponseEntity object containing a list of matching art objects
   */
  @GetMapping("/search")
  public ResponseEntity<List<ArtObject>> searchArtObjects(
      @ModelAttribute ArtObjectSearchCriteria criteria) {
    List<ArtObject> artObjects = artObjectService.searchArtObjects(criteria);
    return ResponseEntity.ok().body(artObjects);
  }

  /**
   * Counts the number of art objects based on the given statuses and user ID.
   *
   * @param statuses The list of statuses to filter the art objects
   * @param userId The ID of the user for whom to count the art objects
   * @return A ResponseEntity object containing the count of art objects as the response body
   */
  @GetMapping("/count")
  public ResponseEntity<Long> countArtObjects(
      @RequestParam List<String> statuses, @RequestParam String userId) {
    long count = artObjectService.countArtObjects(statuses, userId);
    return ResponseEntity.ok(count);
  }
}
