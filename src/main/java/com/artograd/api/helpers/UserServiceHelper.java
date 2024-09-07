package com.artograd.api.helpers;

import com.artograd.api.model.UserAttribute;
import com.artograd.api.repositories.ArtObjectRepository;
import com.artograd.api.repositories.TenderRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceHelper {
  @Autowired private ArtObjectRepository artObjectRepository;
  @Autowired private TenderRepository tenderRepository;
  @Autowired private UserAttributeHelper userAttributeHelper;

  /**
   * Updates art objects with user profile data.
   *
   * @param userName The username.
   * @param attributes The user attributes.
   */
  public void updateUserProfileDataInArtObjects(String userName, List<UserAttribute> attributes) {

    artObjectRepository
        .findByOwnerIdOrSupplierId(userName)
        .forEach(
            ao -> {
              if (ao.getOwner() != null && ao.getOwner().getId().equals(userName)) {
                ao.getOwner().setName(userAttributeHelper.formatUserName(attributes));
                ao.getOwner()
                    .setPicture(userAttributeHelper.getUserAttributeValue(attributes, "picture"));
                ao.getOwner()
                    .setOrganization(
                        userAttributeHelper.getUserAttributeValue(
                            attributes, "custom:organization"));
              }
              if (ao.getSupplier() != null && ao.getSupplier().getId().equals(userName)) {
                ao.getSupplier().setName(userAttributeHelper.formatUserName(attributes));
                ao.getSupplier()
                    .setPicture(userAttributeHelper.getUserAttributeValue(attributes, "picture"));
                ao.getSupplier()
                    .setOrganization(
                        userAttributeHelper.getUserAttributeValue(
                            attributes, "custom:organization"));
              }
              artObjectRepository.save(ao);
            });
  }

  /**
   * Updates tenders with user profile data.
   *
   * @param userName The username.
   * @param attributes The user attributes.
   */
  public void updateUserProfileDataInTendersAndProposals(
      String userName, List<UserAttribute> attributes) {
    tenderRepository
        .findByOwnerIdOrProposalOwnerId(userName)
        .forEach(
            tender -> {
              if (tender.getOwnerId().equals(userName)) {
                tender.setOwnerName(userAttributeHelper.formatUserName(attributes));
                tender.setOwnerPicture(
                    userAttributeHelper.getUserAttributeValue(attributes, "picture"));
                tender.setOrganization(
                    userAttributeHelper.getUserAttributeValue(attributes, "custom:organization"));
              }
              if (tender.getProposals() != null) {
                tender.getProposals().stream()
                    .filter(proposal -> proposal.getOwnerId().equals(userName))
                    .forEach(
                        proposal -> {
                          proposal.setOwnerName(userAttributeHelper.formatUserName(attributes));
                          proposal.setOwnerPicture(
                              userAttributeHelper.getUserAttributeValue(attributes, "picture"));
                          proposal.setOwnerOrg(
                              userAttributeHelper.getUserAttributeValue(
                                  attributes, "custom:organization"));
                        });
              }

              tenderRepository.save(tender);
            });
  }
}
