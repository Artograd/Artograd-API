package com.artograd.api.repositories;

import com.artograd.api.model.ExpenseReport;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExpenseReportRepository extends MongoRepository<ExpenseReport, String> {

  List<ExpenseReport> findByArtObjectIdOrderByDateDesc(String artObjectId);
}
