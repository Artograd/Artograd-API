package com.artograd.api.services.impl;

import com.artograd.api.model.SocialMediaContact;
import com.artograd.api.repositories.SocialMediaContactRepository;
import com.artograd.api.services.ISocialMediaContactService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialMediaContactService implements ISocialMediaContactService {

  @Autowired
  private SocialMediaContactRepository repository;

  @Override
  public List<SocialMediaContact> getAllContacts() {
    return repository.findAll();
  }

  @Override
  public Optional<SocialMediaContact> getContactById(String id) {
    return repository.findById(id);
  }

  @Override
  public SocialMediaContact createContact(SocialMediaContact contact) {
    return repository.save(contact);
  }

  @Override
  public SocialMediaContact updateContact(String id, SocialMediaContact contact) {
    if (repository.existsById(id)) {
      contact.setId(id);
      return repository.save(contact);
    } else {
      return null;
    }
  }

  @Override
  public void deleteContact(String id) {
    repository.deleteById(id);
  }

  @Override
  public List<SocialMediaContact> getContactsByUserId(String userId) {
    return repository.findByUserId(userId);
  }
}
