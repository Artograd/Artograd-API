package com.artograd.api.repositories;

import com.artograd.api.model.WorkUpdate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkUpdateRepository extends MongoRepository<WorkUpdate, String> {

  List<WorkUpdate> findByArtObjectIdOrderByDateDesc(String artObjectId);
}
