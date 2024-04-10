package com.artograd.api.services.impl;

import com.artograd.api.model.Tender;
import com.artograd.api.model.TenderSearchCriteria;
import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;
import com.artograd.api.repositories.TenderRepository;
import com.artograd.api.services.ITenderService;
import com.artograd.api.services.IUserService;
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
public class TenderService implements ITenderService {

  @Autowired private TenderRepository tenderRepository;

  @Autowired private IUserService userService;

  @Autowired private MongoTemplate mongoTemplate;

  /**
   * Creates a new Tender.
   *
   * @param tender The tender to create.
   * @return The created tender.
   */
  @Override
  public Tender createTender(Tender tender) {
    enrichTenderWithOwnerDataAndTimestamps(tender);
    tender.setCreatedAt(new Date());
    return tenderRepository.save(tender);
  }

  /**
   * Retrieves a tender by its ID.
   *
   * @param id The ID of the tender.
   * @return An Optional containing the found tender or empty if not found.
   */
  @Override
  public Optional<Tender> getTender(String id) {
    return tenderRepository.findById(id);
  }

  /**
   * Updates an existing Tender.
   *
   * @param tender The tender to update.
   * @return Optional containing the updated tender if update is successful; otherwise, an empty
   *     Optional.
   */
  @Override
  public Optional<Tender> updateTender(Tender tender) {
    tender.setCreatedAt(getTender(tender.getId()).get().getCreatedAt());
    enrichTenderWithOwnerDataAndTimestamps(tender);
    Tender savedTender = tenderRepository.save(tender);
    return Optional.ofNullable(savedTender);
  }

  /**
   * Deletes a tender by its ID.
   *
   * @param id The ID of the tender to delete.
   */
  @Override
  public void deleteTender(String id) {
    tenderRepository.deleteById(id);
  }

  /**
   * Searches for tenders based on the given criteria.
   *
   * @param criteria The criteria to filter the tenders.
   * @return A list of tenders that match the criteria.
   */
  @Override
  public List<Tender> searchTenders(TenderSearchCriteria criteria) {
    Query query = buildSearchQuery(criteria);
    final Pageable pageable =
        PageRequest.of(
            criteria.getPage(),
            criteria.getSize(),
            Sort.by(Sort.Direction.fromString(criteria.getSortOrder()), criteria.getSortBy()));
    query.with(pageable);
    return mongoTemplate.find(query, Tender.class);
  }

  /**
   * Counts tenders by owner ID and optional statuses.
   *
   * @param ownerId The owner ID.
   * @param statuses Optional list of statuses.
   * @return The count of matching tenders.
   */
  @Override
  public long getCountByOwnerIdAndStatusIn(String ownerId, List<String> statuses) {
    Query query = new Query().addCriteria(Criteria.where("ownerId").is(ownerId));
    if (statuses != null && !statuses.isEmpty()) {
      query.addCriteria(Criteria.where("status").in(statuses));
    }
    return mongoTemplate.count(query, Tender.class);
  }

  /**
   * Checks if a username is the owner of a tender.
   *
   * @param tenderId The tender ID.
   * @param username The username to check.
   * @return True if the username is the owner of the tender.
   */
  @Override
  public boolean isTenderOwner(String tenderId, String username) {
    return getTender(tenderId).map(tender -> tender.getOwnerId().equals(username)).orElse(false);
  }

  private Query buildSearchQuery(TenderSearchCriteria criteria) {
    Query query = new Query();
    List<Criteria> criteriaList = new ArrayList<>();

    if (StringUtils.isNotBlank(criteria.getTitle())) {
      criteriaList.add(Criteria.where("title").regex(criteria.getTitle(), "i"));
    }
    if (StringUtils.isNotBlank(criteria.getOwnerId())) {
      criteriaList.add(Criteria.where("ownerId").is(criteria.getOwnerId()));
    }
    if (!CollectionUtils.isEmpty(criteria.getLocationLeafIds())) {
      criteriaList.add(Criteria.where("locationLeafId").in(criteria.getLocationLeafIds()));
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

  /** Enriches a tender with owner data and updates timestamps. */
  private void enrichTenderWithOwnerDataAndTimestamps(Tender tender) {
    tender.setModifiedAt(new Date());

    if (StringUtils.isNotBlank(tender.getOwnerId())) {
      userService
          .getUserByUsername(tender.getOwnerId())
          .ifPresent(
              user -> {
                tender.setOwnerName(formatUserName(user));
                tender.setOwnerPicture(getUserAttributeValue(user, "picture"));
                tender.setOrganization(getUserAttributeValue(user, "custom:organization"));
              });
    }
  }

  private String formatUserName(User user) {
    String givenName = getUserAttributeValue(user, "given_name");
    String familyName = getUserAttributeValue(user, "family_name");
    return String.format("%s %s", givenName, familyName).trim();
  }

  private String getUserAttributeValue(User user, String attributeName) {
    return user.getAttributes().stream()
        .filter(attr -> attr.getName().equals(attributeName))
        .findFirst()
        .map(UserAttribute::getValue)
        .orElse("");
  }
}
