package com.artograd.api.services.impl;

import com.artograd.api.model.ExpenseReport;
import com.artograd.api.repositories.ExpenseReportRepository;
import com.artograd.api.services.IExpenseReportService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseReportService implements IExpenseReportService {

  @Autowired
  private ExpenseReportRepository expenseReportRepository;

  @Override
  public ExpenseReport createExpenseReport(ExpenseReport expenseReport) {
    return expenseReportRepository.save(expenseReport);
  }

  @Override
  public Optional<ExpenseReport> getExpenseReportById(String id) {
    return expenseReportRepository.findById(id);
  }

  @Override
  public void deleteExpenseReportById(String id) {
    expenseReportRepository.deleteById(id);
  }

  @Override
  public List<ExpenseReport> getExpenseReportsByArtObjectId(String artObjectId) {
    return expenseReportRepository.findByArtObjectIdOrderByDateDesc(artObjectId);
  }
}
