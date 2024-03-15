package com.artograd.api.repositories;

import com.artograd.api.model.Tender;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TenderRepository extends MongoRepository<Tender, String> {
    /*
	@Query("{ 'title': ?0, 'location': { $in: ?1 }, 'status': { $in: ?2 }, 'owner': ?3 }")
    List<Tender> findByTitleLocationStatusOwner(String title, List<String> locations, List<String> statuses, String owner);
    */
}
