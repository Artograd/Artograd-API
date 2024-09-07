package com.artograd.api.repositories;

import com.artograd.api.model.ArtObject;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ArtObjectRepository extends MongoRepository<ArtObject, String> {

  @Query("{ '$or': [ { 'owner.id': ?0 }, { 'supplier.id': ?0 } ] }")
  List<ArtObject> findByOwnerIdOrSupplierId(String ownerId);
}
