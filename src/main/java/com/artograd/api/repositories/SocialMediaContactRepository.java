package com.artograd.api.repositories;

import com.artograd.api.model.SocialMediaContact;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SocialMediaContactRepository extends MongoRepository<SocialMediaContact, String> {
  List<SocialMediaContact> findByUserId(String userId);
  
  Optional<SocialMediaContact> findById(String id);
}
