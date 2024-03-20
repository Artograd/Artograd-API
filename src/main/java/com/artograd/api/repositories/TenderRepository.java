package com.artograd.api.repositories;

import com.artograd.api.model.Tender;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TenderRepository extends MongoRepository<Tender, String> {

}
