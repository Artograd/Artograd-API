package com.artograd.api.services;

import com.artograd.api.model.ExpenseReport;
import java.util.List;
import java.util.Optional;

public interface IExpenseReportService {

  ExpenseReport createExpenseReport(ExpenseReport expenseReport);

  Optional<ExpenseReport> getExpenseReportById(String id);

  void deleteExpenseReportById(String id);

  List<ExpenseReport> getExpenseReportsByArtObjectId(String artObjectId);
}
