package com.artograd.api.controllers;

import com.artograd.api.model.WorkUpdate;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.IArtObjectService;
import com.artograd.api.services.IUserService;
import com.artograd.api.services.IWorkUpdateService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workupdates")
@AllArgsConstructor
public class WorkUpdateController {

  private IWorkUpdateService workUpdateService;
  private IArtObjectService artObjectService;
  private IUserService userService;

  /**
   * Creates a new work update. 
   * This operation is allowed only for the supplier of the related art object.
   *
   * @param workUpdate The work update to be created
   * @param request The HTTP request
   * @return A ResponseEntity representing the status of the creation operation - HttpStatus.CREATED
   *     if the work update is successfully created -
   *     HttpStatus.FORBIDDEN if the user is not the supplier
   */
  @PostMapping
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<?> createWorkUpdate(@RequestBody WorkUpdate workUpdate, 
      HttpServletRequest request) {
    Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);

    return claims
        .flatMap(c -> artObjectService.getArtObject(workUpdate.getArtObjectId()))
        .filter(artObject -> artObject.getSupplier() != null)
        .filter(artObject -> artObject.getSupplier().getId().equals(claims.get().getUsername()))
        .map(artObject -> {
          WorkUpdate createdWorkUpdate = workUpdateService.createWorkUpdate(workUpdate);
          return ResponseEntity.status(HttpStatus.CREATED).body(createdWorkUpdate);
        })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
  }

  /**
   * Retrieves the work update with the specified ID.
   *
   * @param id The ID of the work update to retrieve
   * @return A ResponseEntity object representing the status of the retrieval operation: -
   *     ResponseEntity.ok() with the body set to the retrieved work update if the object exists -
   *     ResponseEntity.notFound() if the object does not exist
   */
  @GetMapping("/{id}")
  public ResponseEntity<WorkUpdate> getWorkUpdateById(@PathVariable String id) {
    return workUpdateService
        .getWorkUpdateById(id)
        .map(workUpdate -> ResponseEntity.ok().body(workUpdate))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Deletes the work update with the specified ID. This operation is allowed 
   * only for the supplier of the related art object.
   *
   * @param id The ID of the work update to delete
   * @param request The HTTP request
   * @return A ResponseEntity representing the status of the delete operation: -
   *     ResponseEntity.noContent() if the work update is successfully deleted -
   *     ResponseEntity.status(HttpStatus.FORBIDDEN) if the user is not the supplier
   */
  @DeleteMapping("/{id}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<?> deleteWorkUpdateById(@PathVariable String id, 
      HttpServletRequest request) {
    Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);

    return workUpdateService.getWorkUpdateById(id)
        .flatMap(workUpdate -> artObjectService.getArtObject(workUpdate.getArtObjectId()))
        .filter(artObject -> artObject.getSupplier() != null)
        .filter(artObject -> artObject.getSupplier().getId().equals(claims.get().getUsername()))
        .map(workUpdate -> {
          workUpdateService.deleteWorkUpdateById(id);
          return ResponseEntity.noContent().build();
        })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
  }

  /**
   * Retrieves the list of work updates for the specified art object, ordered by date descending.
   *
   * @param artObjectId The ID of the art object whose work updates are to be retrieved
   * @return A ResponseEntity object containing a list of matching work updates
   */
  @GetMapping("/artobject/{artObjectId}")
  public ResponseEntity<List<WorkUpdate>> getWorkUpdatesByArtObjectId(
      @PathVariable String artObjectId) {
    List<WorkUpdate> workUpdates = workUpdateService.getWorkUpdatesByArtObjectId(artObjectId);
    return ResponseEntity.ok().body(workUpdates);
  }
}
