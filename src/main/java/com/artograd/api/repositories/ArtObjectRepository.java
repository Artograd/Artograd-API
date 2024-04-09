package com.artograd.api.repositories;

import com.artograd.api.model.ArtObject;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArtObjectRepository extends MongoRepository<ArtObject, String> {}
