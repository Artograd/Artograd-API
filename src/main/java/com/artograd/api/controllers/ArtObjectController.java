package com.artograd.api.controllers;

import com.artograd.api.model.ArtObject;
import com.artograd.api.model.ArtObjectSearchCriteria;
import com.artograd.api.services.IArtObjectService;
import com.artograd.api.services.ITenderService;
import com.artograd.api.services.IUserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.artograd.api.model.system.UserTokenClaims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/artobjects")
public class ArtObjectController {

    @Autowired
    private IArtObjectService artObjectService;
    
    @Autowired
    private IUserService userService;

    @Autowired
    private ITenderService tenderService; 

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createArtObject(@RequestParam String tenderId, @RequestParam String winnerProposalId, HttpServletRequest request) {
        Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);
        
        return claims.filter(UserTokenClaims::isOfficer)
                     .flatMap(c -> tenderService.getTender(tenderId))
                     .filter(tender -> tender.getOwnerId().equals(claims.get().getUsername()))
                     .flatMap(tender -> artObjectService.createArtObject(tenderId, winnerProposalId))
                     .map(artObject -> ResponseEntity.status(HttpStatus.CREATED).body(artObject))
                     .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtObject> getArtObject(@PathVariable String id) {
        return artObjectService.getArtObject(id)
            .map(artObject -> ResponseEntity.ok().body(artObject))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateArtObject(@PathVariable String id, @RequestBody ArtObject artObject, HttpServletRequest request) {
        Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);

        return claims.filter(UserTokenClaims::isOfficer)
                     .flatMap(c -> tenderService.getTender(artObject.getTender().getId()))
                     .filter(tender -> tender.getOwnerId().equals(claims.get().getUsername()))
                     .flatMap(tender -> artObjectService.updateArtObject(id, artObject))
                     .map(updatedArtObject -> ResponseEntity.ok().body(updatedArtObject))
                     .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteArtObject(@PathVariable String id, HttpServletRequest request) {
        Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);
        
        return claims.filter(UserTokenClaims::isOfficer)
                     .flatMap(c -> artObjectService.getArtObject(id))
                     .filter(artObject -> tenderService.getTender(
                    		 artObject.getTender().getId()).get().getOwnerId().equals(claims.get().getUsername())) 
                     .map(artObject -> {
                         artObjectService.deleteArtObject(id);
                         return ResponseEntity.noContent().<Void>build();
                     })
                     .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArtObject>> searchArtObjects(@ModelAttribute ArtObjectSearchCriteria criteria) {
        List<ArtObject> artObjects = artObjectService.searchArtObjects(criteria);
        return ResponseEntity.ok().body(artObjects);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countArtObjects(@RequestParam List<String> statuses, @RequestParam String userId) {
        long count = artObjectService.countArtObjects(statuses, userId);
        return ResponseEntity.ok(count);
    }
}
