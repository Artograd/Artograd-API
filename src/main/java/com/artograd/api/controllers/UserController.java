package com.artograd.api.controllers;

import com.artograd.api.model.UserAttribute;
import com.artograd.api.services.IUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
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
public class UserController {

  @Autowired private IUserService userService;

  /**
   * Retrieves the user attributes by username.
   *
   * @param username The username of the user.
   * @return A ResponseEntity object representing the HTTP response with the user attributes if
   *         found, or a ResponseEntity object with status 404 if the user is not found.
   */
  @GetMapping("/{username}")
  public ResponseEntity<?> getUserAttributesByUsername(@PathVariable String username) {
    return userService
        .getUserByUsername(username)
        .map(user -> ResponseEntity.ok().body(user))
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Deletes a user by their username.
   *
   * @param username The username of the user to be deleted.
   * @param request The HttpServletRequest object representing the HTTP request.
   * @return A ResponseEntity object representing the HTTP response with status 204 (No Content)
   *         if the user is successfully deleted, or a ResponseEntity object with status 500
   *         (Internal Server Error) and an error message if an error occurs during the deletion.
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
  } //TODO: might need another answer than 500

  /**
   * Update the attributes of a user by their username.
   *
   * @param username The username of the user.
   * @param attributes The list of user attributes to update.
   * @param request The HttpServletRequest object representing the HTTP request.
   * @return A ResponseEntity object representing the HTTP response with status 200 (OK)
   *         if the user attributes are successfully updated, or a ResponseEntity object
   *         with status 500 (Internal Server Error) and an error message if an error occurs
   *         during the update.
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
  } //TODO: might need another answer than 500

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
        .map(claims -> !username.equals(claims.getUsername()))
        .orElse(true); // Deny access if token is not present or username does not match
  }
}
