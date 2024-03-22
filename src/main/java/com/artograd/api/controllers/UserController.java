package com.artograd.api.controllers;

import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.CognitoService;
import com.artograd.api.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

  @Autowired private CognitoService cognitoService;

  @GetMapping("/{username}")
  public ResponseEntity<Map<String, Object>> getUserAttributesByUsername(
      @PathVariable String username) {
    User user = cognitoService.getUserByUsername(username);
    if (user != null) {
      return ResponseHandler.generateResponse(user, HttpStatus.OK);
    } else {
      return ResponseHandler.generateResponse(HttpStatus.NOT_FOUND, "User not found");
    }
  }

  @DeleteMapping("/{username}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Map<String, Object>> deleteUserById(
      @PathVariable String username, HttpServletRequest request) {

    if (isDenied(username, request)) {
      return ResponseHandler.generateResponse(HttpStatus.FORBIDDEN);
    }

    boolean success = cognitoService.deleteUserByUsername(username);
    if (success) {
      return ResponseHandler.generateResponse(HttpStatus.NO_CONTENT);
    } else {
      return ResponseHandler.generateResponse(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting user");
    }
  }

  @PutMapping("/{username}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Map<String, Object>> updateUserAttributesById(
      @PathVariable String username,
      @RequestBody List<UserAttribute> attributes,
      HttpServletRequest request) {

    if (isDenied(username, request)) {
      return ResponseHandler.generateResponse(HttpStatus.FORBIDDEN);
    }

    boolean success = cognitoService.updateUserAttributes(username, attributes);
    if (success) {
      return ResponseHandler.generateResponse(HttpStatus.OK);
    } else {
      return ResponseHandler.generateResponse(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error updating user attributes");
    }
  }

  /**
   * Check that operation is executed by profile owner
   *
   * @param username
   * @param request
   * @return
   */
  private boolean isDenied(String username, HttpServletRequest request) {
    UserTokenClaims claims = cognitoService.getUserTokenClaims(request);
    // request is made on behalf of different user
    return claims.getUsername() == null
        || // username is not provided in token
        !claims.getUsername().equals(username);
  }
}
