package com.artograd.api.controllers;

import com.artograd.api.model.UserAttribute;
import com.artograd.api.model.enums.UserRole;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.IUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

  private IUserService userService;

  /**
   * Retrieves the user attributes by the provided username.
   *
   * @param username the username of the user
   * @param request the HttpServletRequest object
   * @return the ResponseEntity object containing the list of filtered user attributes, or a 404 Not
   *     Found response if the user is not found
   */
  @GetMapping("/{username}")
  public ResponseEntity<List<UserAttribute>> getUserAttributesByUsername(
      @PathVariable String username, HttpServletRequest request) {
    return userService
        .getUserByUsername(username)
        .map(
            user -> {
              UserTokenClaims claims =
                  userService.getUserTokenClaims(request).orElseGet(UserTokenClaims::new);
              boolean isProfileOwner = username.equals(claims.getUsername());
              UserRole requesterRole = claims.getUserRole();

              List<UserAttribute> filteredAttributes =
                  userService.filterAttributes(
                      user.getAttributes(), requesterRole, isProfileOwner, user.getRole());

              return ResponseEntity.ok().body(filteredAttributes);
            })
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Deletes a user by their username.
   *
   * @param username the username of the user to delete
   * @param request the HttpServletRequest object
   * @return a ResponseEntity object indicating the status of the operation
   */
  @DeleteMapping("/{username}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<?> deleteUserByUsername(
      @PathVariable String username, HttpServletRequest request) {
    if (isDenied(username, request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
    }

    return userService.deleteUserByUsername(username)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.internalServerError().body("Error deleting user.");
  }

  /**
   * Updates the attributes of a user by their username.
   *
   * @param username the username of the user
   * @param attributes the new attributes for the user
   * @param request the HttpServletRequest object
   * @return a ResponseEntity indicating the status of the operation
   */
  @PutMapping("/{username}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<?> updateUserAttributesByUsername(
      @PathVariable String username,
      @RequestBody List<UserAttribute> attributes,
      HttpServletRequest request) {
    if (isDenied(username, request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
    }

    return userService.updateUserAttributes(username, attributes)
        ? ResponseEntity.ok().build()
        : ResponseEntity.internalServerError().body("Error updating user attributes.");
  }

  /**
   * Check that operation is executed by profile owner.
   *
   * @param username the username to check against the token
   * @param request the HTTP request containing the authentication token
   * @return true if access is denied, false otherwise
   */
  private boolean isDenied(String username, HttpServletRequest request) {
    return userService
        .getUserTokenClaims(request)
        .map(claims -> !username.equalsIgnoreCase(claims.getUsername()))
        .orElse(true); // Deny access if token is not present or username does not match
  }
}
