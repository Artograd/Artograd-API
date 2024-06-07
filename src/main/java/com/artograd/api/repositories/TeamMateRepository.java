package com.artograd.api.repositories;

import com.artograd.api.model.TeamMate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamMateRepository extends MongoRepository<TeamMate, String> {
  List<TeamMate> findByActiveTrue();
}
