package com.artograd.api.services.impl;

import com.artograd.api.helpers.UserAttributeHelper;
import com.artograd.api.model.ArtObject;
import com.artograd.api.model.ArtObjectSearchCriteria;
import com.artograd.api.model.BudgetInfo;
import com.artograd.api.model.PaymentInfo;
import com.artograd.api.model.Proposal;
import com.artograd.api.model.Tender;
import com.artograd.api.model.UserInfo;
import com.artograd.api.model.enums.UserAttributeKey;
import com.artograd.api.repositories.ArtObjectRepository;
import com.artograd.api.services.IArtObjectService;
import com.artograd.api.services.IProposalService;
import com.artograd.api.services.ITenderService;
import com.artograd.api.services.IUserService;
import com.artograd.api.services.system.SequenceGeneratorService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class ArtObjectService implements IArtObjectService {

  @Autowired private ArtObjectRepository artObjectRepository;

  @Autowired private ITenderService tenderService;

  @Autowired private IProposalService proposalService;

  @Autowired private IUserService userService;

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired private SequenceGeneratorService sequenceGeneratorService;

  @Autowired private UserAttributeHelper userAttributeHelper;

  @Override
  public Optional<ArtObject> createArtObject(String tenderId, String winnerProposalId) {
    return tenderService
        .getTender(tenderId)
        .flatMap(
            tender ->
                proposalService
                    .getProposal(tenderId, winnerProposalId)
                    .map(
                        proposal -> {
                          ArtObject artObject = new ArtObject();

                          setArtObjectFromTenderAndProposal(artObject, tender, proposal);
                          enrichArtObjectWithUserData(artObject, tender, proposal);

                          artObject.setCreatedAt(new Date());
                          artObject.setStatus("NEW");

                          return artObjectRepository.save(artObject);
                        }));
  }

  @Override
  public Optional<ArtObject> updateArtObject(String id, ArtObject artObject) {
    if (artObjectRepository.existsById(id)) {
      return Optional.of(artObjectRepository.save(artObject));
    }
    return Optional.empty();
  }

  @Override
  public void deleteArtObject(String id) {
    artObjectRepository.deleteById(id);
  }

  @Override
  public Optional<ArtObject> getArtObject(String id) {
    return artObjectRepository.findById(id);
  }

  @Override
  public List<ArtObject> searchArtObjects(ArtObjectSearchCriteria criteria) {
    Query query = buildSearchQuery(criteria);
    final Pageable pageable =
        PageRequest.of(
            criteria.getPage(),
            criteria.getSize(),
            Sort.by(Sort.Direction.fromString(criteria.getSortOrder()), criteria.getSortBy()));
    query.with(pageable);
    return mongoTemplate.find(query, ArtObject.class);
  }

  private Query buildSearchQuery(ArtObjectSearchCriteria criteria) {
    Query query = new Query();
    List<Criteria> criteriaList = new ArrayList<>();

    if (StringUtils.isNotBlank(criteria.getTitle())) {
      criteriaList.add(Criteria.where("title").regex(criteria.getTitle(), "i"));
    }
    if (StringUtils.isNotBlank(criteria.getUserId())) {
      Criteria ownerCriteria = Criteria.where("owner.id").is(criteria.getUserId());
      Criteria supplierCriteria = Criteria.where("supplier.id").is(criteria.getUserId());
      criteriaList.add(new Criteria().orOperator(ownerCriteria, supplierCriteria));
    }

    if (!CollectionUtils.isEmpty(criteria.getStatuses())) {
      criteriaList.add(Criteria.where("status").in(criteria.getStatuses()));
    }

    if (!criteriaList.isEmpty()) {
      Criteria combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
      query.addCriteria(combinedCriteria);
    }

    Sort.Direction direction = Sort.Direction.fromString(criteria.getSortOrder());
    Sort sort = Sort.by(direction, criteria.getSortBy());
    query.with(sort);

    return query;
  }

  @Override
  public long countArtObjects(List<String> statuses, String userId) {
    Query query = new Query();

    if (statuses != null && !statuses.isEmpty()) {
      query.addCriteria(Criteria.where("status").in(statuses));
    }

    if (StringUtils.isNotBlank(userId)) {
      Criteria ownerCriteria = Criteria.where("owner.id").is(userId);
      Criteria supplierCriteria = Criteria.where("supplier.id").is(userId);
      query.addCriteria(new Criteria().orOperator(ownerCriteria, supplierCriteria));
    }
    return mongoTemplate.count(query, ArtObject.class);
  }

  @Override
  public boolean isArtObjectOwner(String objectId, String username) {
    return getArtObject(objectId)
        .map(object -> object.getOwner().getId().equals(username))
        .orElse(false);
  }

  @Override
  public Optional<ArtObject> patchArtObject(String id, ArtObject artObject) {
    return artObjectRepository
        .findById(id)
        .flatMap(
            ao -> {
              if (artObject.getTitle() != null) {
                ao.setTitle(artObject.getTitle());
              }
              if (artObject.getTopContributors() != null) {
                ao.setTopContributors(artObject.getTopContributors());
              }
              if (artObject.getDescription() != null) {
                ao.setDescription(artObject.getDescription());
              }
              if (artObject.getFiles() != null) {
                ao.setFiles(artObject.getFiles());
              }
              if (artObject.getCover() != null) {
                ao.setCover(artObject.getCover());
              }
              if (artObject.getBudget() != null) {
                ao.setBudget(artObject.getBudget());
              }
              if (artObject.getStatus() != null) {
                ao.setStatus(artObject.getStatus());
              }
              if (artObject.getCategory() != null) {
                ao.setCategory(artObject.getCategory());
              }
              if (artObject.getLocation() != null) {
                ao.setLocation(artObject.getLocation());
              }
              if (artObject.getLocationLeafId() != null) {
                ao.setLocationLeafId(artObject.getLocationLeafId());
              }
              if (artObject.getDeliveryDate() != null) {
                ao.setDeliveryDate(artObject.getDeliveryDate());
              }
              if (artObject.getSupplier() != null) {
                ao.setSupplier(artObject.getSupplier());
              }
              if (artObject.getPayment() != null) {
                ao.setPayment(artObject.getPayment());
              }
              return Optional.of(artObjectRepository.save(ao));
            });
  }

  private void setArtObjectFromTenderAndProposal(
      ArtObject artObject, Tender tender, Proposal proposal) {
    artObject.setTitle(tender.getTitle());
    artObject.setDescription(tender.getDescription());
    artObject.setDeliveryDate(tender.getExpectedDelivery());
    artObject.setCategory(tender.getCategory());
    artObject.setFiles(proposal.getFiles());
    artObject.setCover(proposal.getCover());
    artObject.setLocation(tender.getLocation());
    artObject.setLocationLeafId(tender.getLocationLeafId());

    BudgetInfo budgetInfo = new BudgetInfo();
    budgetInfo.setInitialEstimate(proposal.getEstimatedCost());
    budgetInfo.setCurrentEstimate(proposal.getEstimatedCost());
    budgetInfo.setFundraisingTarget(proposal.getEstimatedCost());
    artObject.setBudget(budgetInfo);

    PaymentInfo pi = new PaymentInfo();
    String articul = sequenceGeneratorService.generateSequence("art_object_sequence");
    pi.setArticul(articul);
    artObject.setPayment(pi);

    Tender t = new Tender();
    t.setId(tender.getId());
    t.setTitle(tender.getTitle());
    artObject.setTender(t);
  }

  private void enrichArtObjectWithUserData(ArtObject artObject, Tender tender, Proposal proposal) {
    artObject.setOwner(enrichUserInfo(tender.getOwnerId()));
    artObject.setSupplier(enrichUserInfo(proposal.getOwnerId()));
  }

  private UserInfo enrichUserInfo(String userId) {
    return userService
        .getUserByUsername(userId)
        .map(
            user -> {
              UserInfo userInfo = new UserInfo();
              userInfo.setId(userId);
              userInfo.setName(userAttributeHelper.formatUserName(user));
              userInfo.setPicture(
                  userAttributeHelper.getUserAttributeValue(user, UserAttributeKey.PICTURE));
              userInfo.setOrganization(
                  userAttributeHelper.getUserAttributeValue(
                      user, UserAttributeKey.CUSTOM_ORGANIZATION));
              return userInfo;
            })
        .orElse(null);
  }
}
