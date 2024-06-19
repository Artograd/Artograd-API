package com.artograd.api.controllers;

import com.artograd.api.model.Tender;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.IEmailService;
import com.artograd.api.services.ITenderService;
import com.artograd.api.services.IUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

  @Autowired
  private IEmailService emailService;

  @Autowired
  private ITenderService tenderService;

  @Autowired
  private IUserService userService;

  /**
   * Send emails to contacts of tender owner.
   * @param tenderId tender id
   * @param request from clien
   * @return
   */
  @PostMapping("/tender/published/{tenderId}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<String> sendTenderEmails(
          @PathVariable String tenderId, HttpServletRequest request) {
    UserTokenClaims claims = userService.getUserTokenClaims(request).orElse(null);
    if (claims == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
    }

    Tender tender = tenderService.getTender(tenderId).orElse(null);
    if (tender == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tender not found");
    }

    if (!claims.getUsername().equals(tender.getOwnerId())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            "You do not have permission to send emails for this tender");
    }

    emailService.sendEmailsTenderIsPublished(tender);
    return ResponseEntity.ok("Emails sent successfully");
  }
}
