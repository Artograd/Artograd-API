package com.artograd.api.controllers;

import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.CognitoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {
	
	@Autowired
    private CognitoService cognitoService;
    
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserAttributesByUsername(@PathVariable String username) {
        User user = cognitoService.getUserByUsername(username);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{username}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteUserById(@PathVariable String username, HttpServletRequest request) {
    	
    	if ( isDenied(username, request) ) { return new ResponseEntity<>(HttpStatus.FORBIDDEN);}
    	
        boolean success = cognitoService.deleteUserByUsername(username);
        if (success) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("Error deleting user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{username}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateUserAttributesById(@PathVariable String username, @RequestBody List<UserAttribute> attributes, HttpServletRequest request) {
    	
    	if ( isDenied(username, request) ) { return new ResponseEntity<>(HttpStatus.FORBIDDEN);}
    	
        boolean success = cognitoService.updateUserAttributes(username, attributes);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error updating user attributes", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Check that operation is executed by profile owner 
     * @param username
     * @param request
     * @return
     */
    private boolean isDenied(String username, HttpServletRequest request) {
    	UserTokenClaims claims = cognitoService.getUserTokenClaims(request);
        //request is made on behalf of different user
        return claims.getUsername() == null || //username is not provided in token
               !claims.getUsername().equals(username);
    }
}
