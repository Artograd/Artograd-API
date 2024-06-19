package com.artograd.api.services;

import com.artograd.api.model.SocialMediaContact;
import java.util.List;
import java.util.Optional;

public interface ISocialMediaContactService {
  List<SocialMediaContact> getAllContacts();
  
  Optional<SocialMediaContact> getContactById(String id);
  
  SocialMediaContact createContact(SocialMediaContact contact);
  
  SocialMediaContact updateContact(String id, SocialMediaContact contact);
  
  void deleteContact(String id);
  
  List<SocialMediaContact> getContactsByUserId(String userId);
}
