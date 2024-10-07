package com.artograd.api.repositories;

import com.artograd.api.model.Tender;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TenderRepository extends MongoRepository<Tender, String> {

  @Query("{ '$or': [ { 'ownerId': ?0 }, { 'proposals.ownerId': ?0 } ] }")
  List<Tender> findByOwnerIdOrProposalOwnerId(String ownerId);

  @Query("{ 'submissionStart': ?0, 'status': ?1 }")
  List<Tender> findBySubmissionStartAndStatus(Date submissionStart, String status);
}
