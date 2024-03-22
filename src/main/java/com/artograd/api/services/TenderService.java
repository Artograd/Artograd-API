package com.artograd.api.services;

import com.artograd.api.model.Tender;
import com.artograd.api.model.User;
import com.artograd.api.model.UserAttribute;
import com.artograd.api.repositories.TenderRepository;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TenderService {

 	@Autowired
    private TenderRepository tenderRepository;

    @Autowired
    private CognitoService cognitoService;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Creates a new Tender.
     *
     * @param tender The tender to create.
     * @return The created tender.
     */
    public Tender createTender(Tender tender) {
        enrichTenderWithOwnerDataAndTimestamps(tender, true);
        return tenderRepository.save(tender);
    }

    /**
     * Retrieves a tender by its ID.
     *
     * @param id The ID of the tender.
     * @return An Optional containing the found tender or empty if not found.
     */
    public Optional<Tender> getTender(String id) {
        return tenderRepository.findById(id);
    }

    /**
     * Updates an existing Tender.
     *
     * @param tender     The tender to update.
     * @param updateDate If true, updates the modifiedAt timestamp to now.
     * @return Optional containing the updated tender if update is successful; otherwise, an empty Optional.
     */
    public Optional<Tender> updateTender(Tender tender, boolean updateDate) {
        enrichTenderWithOwnerDataAndTimestamps(tender, updateDate);
        Tender savedTender = tenderRepository.save(tender);
        return Optional.ofNullable(savedTender);
    }

    /**
     * Deletes a tender by its ID.
     *
     * @param id The ID of the tender to delete.
     */
    public void deleteTender(String id) {
        tenderRepository.deleteById(id);
    }

    /**
     * Searches for tenders based on various criteria.
     *
     * @param title            The title to search for.
     * @param locationLeafIds  The location leaf IDs.
     * @param statuses         The statuses.
     * @param ownerId          The owner ID.
     * @param page             The page number for pagination.
     * @param size             The page size for pagination.
     * @param sortBy           The field to sort by.
     * @param sortOrder        The sort order, either 'asc' or 'desc'.
     * @return A list of tenders that match the criteria.
     */
    public List<Tender> searchTenders(String title, List<String> locationLeafIds, List<String> statuses,
                                      String ownerId, int page, int size, String sortBy, String sortOrder) {
        Query query = buildSearchQuery(title, locationLeafIds, statuses, ownerId, sortBy, sortOrder);
        final Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        query.with(pageable);
        return mongoTemplate.find(query, Tender.class);
    }

    /**
     * Counts tenders by owner ID and optional statuses.
     *
     * @param ownerId  The owner ID.
     * @param statuses Optional list of statuses.
     * @return The count of matching tenders.
     */
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
    public boolean isTenderOwner(String tenderId, String username) {
        return getTender(tenderId)
                .map(tender -> tender.getOwnerId().equals(username))
                .orElse(false);
    }

    /**
     * Builds a Query object for searching tenders based on criteria.
     */
    private Query buildSearchQuery(String title, List<String> locationLeafIds, List<String> statuses, String ownerId, String sortBy, String sortOrder) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (StringUtils.isNotBlank(title)) {
            criteriaList.add(Criteria.where("title").regex(title, "i"));
        }
        if (StringUtils.isNotBlank(ownerId)) {
            criteriaList.add(Criteria.where("ownerId").is(ownerId));
        }
        if (!CollectionUtils.isEmpty(locationLeafIds)) {
            criteriaList.add(Criteria.where("locationLeafId").in(locationLeafIds));
        }
        if (!CollectionUtils.isEmpty(statuses)) {
            criteriaList.add(Criteria.where("status").in(statuses));
        }

        if (!criteriaList.isEmpty()) {
            Criteria criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
            query.addCriteria(criteria);
        }

        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Sort sort = Sort.by(direction, sortBy);
        query.with(sort);

        return query;
    }


    /**
     * Enriches a tender with owner data and updates timestamps.
     */
    private void enrichTenderWithOwnerDataAndTimestamps(Tender tender, boolean updateDate) {
        if (updateDate) {
            tender.setModifiedAt(new Date());
            if (tender.getCreatedAt() == null) {
                tender.setCreatedAt(tender.getModifiedAt());
            }
        }

        if (StringUtils.isNotBlank(tender.getOwnerId())) {
            cognitoService.getUserByUsername(tender.getOwnerId())
                .ifPresent(user -> {
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
