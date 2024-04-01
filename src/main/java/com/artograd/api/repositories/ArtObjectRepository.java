package com.artograd.api.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.artograd.api.model.ArtObject;

public interface ArtObjectRepository extends MongoRepository<ArtObject, String> {

}