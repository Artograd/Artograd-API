package com.artograd.api.controllers;

import com.artograd.api.model.UserAttribute;
import com.artograd.api.model.enums.UserRole;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.IUserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<List<UserAttribute>> getUserAttributesByUsername(@PathVariable String username, HttpServletRequest request) {
        return userService.getUserByUsername(username)
                .map(user -> {
                    UserTokenClaims claims = userService.getUserTokenClaims(request).orElseGet(UserTokenClaims::new);
                    boolean isProfileOwner = username.equals(claims.getUsername());
                    UserRole requesterRole = claims.getUserRole();

                    List<UserAttribute> filteredAttributes = userService.filterAttributes(
                            user.getAttributes(), requesterRole, isProfileOwner, user.getRole());

                    return ResponseEntity.ok().body(filteredAttributes);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{username}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteUserByUsername(@PathVariable String username, HttpServletRequest request) {
        if (isDenied(username, request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }

        return userService.deleteUserByUsername(username) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.internalServerError().body("Error deleting user.");
    }

    @PutMapping("/{username}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateUserAttributesByUsername(@PathVariable String username, @RequestBody List<UserAttribute> attributes, HttpServletRequest request) {
        if (isDenied(username, request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }

        return userService.updateUserAttributes(username, attributes) ?
                ResponseEntity.ok().build() :
                ResponseEntity.internalServerError().body("Error updating user attributes.");
    }

    /**
     * Check that operation is executed by profile owner
     *
     * @param username the username to check against the token
     * @param request  the HTTP request containing the authentication token
     * @return true if access is denied, false otherwise
     */
    private boolean isDenied(String username, HttpServletRequest request) {
        return userService.getUserTokenClaims(request)
                .map(claims -> !username.equalsIgnoreCase(claims.getUsername()))
                .orElse(true); // Deny access if token is not present or username does not match
    }
}
