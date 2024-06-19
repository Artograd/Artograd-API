package com.artograd.api.controllers;

import com.artograd.api.model.SocialMediaContact;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.ISocialMediaContactService;
import com.artograd.api.services.IUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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


@RestController
@RequestMapping("/api/contacts")
public class SocialMediaContactController {

  @Autowired
  private ISocialMediaContactService service;

  @Autowired
  private IUserService userService;

  /**
   * Retrieve a social media contact by ID.
   *
   * @param id the ID of the contact to retrieve
   * @param request the HTTP request object
   * @return the contact if found and the user is authorized, otherwise a forbidden status
   */
  @GetMapping("/{id}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<SocialMediaContact> getContactById(
          @PathVariable String id, HttpServletRequest request) {
    Optional<UserTokenClaims> userTokenClaimsOpt = userService.getUserTokenClaims(request);
    if (userTokenClaimsOpt.isPresent()) {
      String userId = userTokenClaimsOpt.get().getUsername();
      Optional<SocialMediaContact> contactOpt = service.getContactById(id);
      if (contactOpt.isPresent() && contactOpt.get().getUserId().equals(userId)) {
        return ResponseEntity.ok(contactOpt.get());
      } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  /**
   * Create a new social media contact.
   *
   * @param contact the contact to create
   * @param request the HTTP request object
   * @return the created contact if the user is authorized, otherwise a forbidden status
   */
  @PostMapping
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<SocialMediaContact> createContact(
          @RequestBody SocialMediaContact contact, HttpServletRequest request) {
    Optional<UserTokenClaims> userTokenClaimsOpt = userService.getUserTokenClaims(request);
    if (userTokenClaimsOpt.isPresent()) {
      String userId = userTokenClaimsOpt.get().getUsername();
      contact.setUserId(userId);
      SocialMediaContact createdContact = service.createContact(contact);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdContact);
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  /**
   * Update an existing social media contact.
   *
   * @param id the ID of the contact to update
   * @param contact the updated contact details
   * @param request the HTTP request object
   * @return the updated contact if the user is authorized, otherwise a forbidden status
   */
  @PutMapping("/{id}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<SocialMediaContact> updateContact(
          @PathVariable String id, @RequestBody SocialMediaContact contact, 
          HttpServletRequest request) {
    Optional<UserTokenClaims> userTokenClaimsOpt = userService.getUserTokenClaims(request);
    if (userTokenClaimsOpt.isPresent()) {
      String userId = userTokenClaimsOpt.get().getUsername();
      Optional<SocialMediaContact> existingContactOpt = service.getContactById(id);
      if (existingContactOpt.isPresent() && existingContactOpt.get().getUserId().equals(userId)) {
        contact.setId(id);
        contact.setUserId(userId);
        SocialMediaContact updatedContact = service.updateContact(id, contact);
        return ResponseEntity.ok(updatedContact);
      } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  /**
   * Delete a social media contact by ID.
   *
   * @param id the ID of the contact to delete
   * @param request the HTTP request object
   * @return no content if the deletion is successful and the user is authorized
   */
  @DeleteMapping("/{id}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Void> deleteContact(@PathVariable String id, HttpServletRequest request) {
    Optional<UserTokenClaims> userTokenClaimsOpt = userService.getUserTokenClaims(request);
    if (userTokenClaimsOpt.isPresent()) {
      String userId = userTokenClaimsOpt.get().getUsername();
      Optional<SocialMediaContact> contactOpt = service.getContactById(id);
      if (contactOpt.isPresent() && contactOpt.get().getUserId().equals(userId)) {
        service.deleteContact(id);
        return ResponseEntity.noContent().build();
      } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  /**
   * Retrieve all social media contacts for a specific user.
   *
   * @param userId the ID of the user
   * @param request the HTTP request object
   * @return a list of contacts if the user is authorized, otherwise a forbidden status
   */
  @GetMapping("/user/{userId}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<List<SocialMediaContact>> getContactsByUserId(
          @PathVariable String userId, HttpServletRequest request) {
    Optional<UserTokenClaims> userTokenClaimsOpt = userService.getUserTokenClaims(request);
    if (userTokenClaimsOpt.isPresent() && userTokenClaimsOpt.get().getUsername().equals(userId)) {
      List<SocialMediaContact> contacts = service.getContactsByUserId(userId);
      return ResponseEntity.ok(contacts);
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }
}