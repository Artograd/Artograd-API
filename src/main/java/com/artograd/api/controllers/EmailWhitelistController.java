package com.artograd.api.controllers;

import com.artograd.api.model.EmailWhitelistEntry;
import com.artograd.api.repositories.EmailWhitelistRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/email-whitelist")
public class EmailWhitelistController {

  @Autowired
  private EmailWhitelistRepository emailWhitelistRepository;

  @GetMapping
  public ResponseEntity<List<EmailWhitelistEntry>> getAllWhitelistEntries(
        HttpServletRequest request) {
    List<EmailWhitelistEntry> entries = emailWhitelistRepository.findAll();
    return ResponseEntity.ok(entries);
  }

  @PostMapping
  public ResponseEntity<EmailWhitelistEntry> addWhitelistEntry(
        @RequestBody EmailWhitelistEntry entry, HttpServletRequest request) {
    EmailWhitelistEntry savedEntry = emailWhitelistRepository.save(entry);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedEntry);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWhitelistEntry(@PathVariable String id, 
        HttpServletRequest request) {
    emailWhitelistRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
