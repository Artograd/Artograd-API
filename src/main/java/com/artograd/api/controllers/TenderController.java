package com.artograd.api.controllers;

import com.artograd.api.model.Tender;
import com.artograd.api.services.CognitoService;
import com.artograd.api.services.TenderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenders")
public class TenderController {

    @Autowired
    private TenderService tenderService;
	
    @Autowired
    private CognitoService cognitoService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Tender> createTender(@RequestBody Tender tender, HttpServletRequest request) {
        return cognitoService.getUserTokenClaims(request)
            .filter(claims -> claims.getUsername() != null)
            .filter(claims -> claims.getUsername().equals(tender.getOwnerId()) && claims.isOfficer())
            .map(claims -> tenderService.createTender(tender))
            .map(createdTender -> ResponseEntity.status(HttpStatus.CREATED).body(createdTender))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tender> getTender(@PathVariable String id) {
        return tenderService.getTender(id)
            .map(tender -> ResponseEntity.ok().body(tender))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Tender> updateTender(@PathVariable String id, @RequestBody Tender tender, HttpServletRequest request) {
        return isDenied(id, request) ?
            ResponseEntity.status(HttpStatus.FORBIDDEN).build() :
            tenderService.updateTender(tender)
                .map(updatedTender -> ResponseEntity.ok().body(updatedTender))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteTender(@PathVariable String id, HttpServletRequest request) {
        if (isDenied(id, request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        tenderService.deleteTender(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Tender>> searchTenders(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) List<String> locationLeafIds,
        @RequestParam(required = false) List<String> statuses,
        @RequestParam(required = false) String ownerId,
        @RequestParam(defaultValue = "0") int page, 
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy, 
        @RequestParam(defaultValue = "desc") String sortOrder) {

        List<Tender> tenders = tenderService.searchTenders(title, locationLeafIds, statuses, ownerId, page, size, sortBy, sortOrder);
        return ResponseEntity.ok().body(tenders);
    }

    @GetMapping("/count/{ownerId}")
    public ResponseEntity<Long> getTenderCountByOwnerIdAndStatuses(
            @PathVariable String ownerId, @RequestParam(required = false) List<String> statuses) {
        long count = tenderService.getCountByOwnerIdAndStatusIn(ownerId, statuses);
        return ResponseEntity.ok(count);
    }

    private boolean isDenied(String tenderId, HttpServletRequest request) {
        return cognitoService.getUserTokenClaims(request)
            .map(claims -> !claims.isOfficer() || !tenderService.isTenderOwner(tenderId, claims.getUsername()))
            .orElse(true);
    }
}

