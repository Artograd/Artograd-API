package com.artograd.api.repositories;

import com.artograd.api.model.EmailWhitelistEntry;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailWhitelistRepository extends MongoRepository<EmailWhitelistEntry, String> {

  List<EmailWhitelistEntry> findByEmailIgnoreCase(String email);
  
  List<EmailWhitelistEntry> findByDomainIgnoreCase(String domain);
}
