package com.artograd.api.repositories;

import com.artograd.api.model.City;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CitiesRepository extends MongoRepository<City, String> {
    // No additional methods required for basic CRUD operations
}