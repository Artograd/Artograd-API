package com.artograd.api.services.system;

import com.artograd.api.model.system.DatabaseSequence;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SequenceGeneratorService {

  private MongoOperations mongoOperations;

  /**
   * Generates a sequence number based on the given sequence name.
   *
   * @param seqName the name of the sequence
   * @return the generated sequence number
   */
  public String generateSequence(String seqName) {
    DatabaseSequence counter =
        mongoOperations.findAndModify(
            Query.query(Criteria.where("_id").is(seqName)),
            new Update().inc("seq", 1),
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            DatabaseSequence.class);

    long sequenceValue = counter != null ? counter.getSeq() : 1;
    return String.format("%03d-%03d", sequenceValue / 1000, sequenceValue % 1000);
  }
}
