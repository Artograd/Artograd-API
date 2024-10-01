package com.artograd.api.services.impl;

import com.artograd.api.model.EmailWhitelistEntry;
import com.artograd.api.repositories.EmailWhitelistRepository;
import com.artograd.api.services.IEmailWhitelistService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailWhitelistService implements IEmailWhitelistService {

  @Autowired
  private EmailWhitelistRepository emailWhitelistRepository;

  @Override
  public boolean isEmailWhitelisted(String email) {
    if (email == null || !email.contains("@")) {
      return false;
    }

    String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
    String emailLower = email.toLowerCase();

    List<EmailWhitelistEntry> emailEntries = 
        emailWhitelistRepository.findByEmailIgnoreCase(emailLower);
    if (!emailEntries.isEmpty()) {
      return true;
    }

    List<EmailWhitelistEntry> domainEntries = 
        emailWhitelistRepository.findByDomainIgnoreCase(domain);
    return !domainEntries.isEmpty();
  }
}
