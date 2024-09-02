package com.artograd.api.controllers;

import com.artograd.api.model.ExpenseReport;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.IArtObjectService;
import com.artograd.api.services.IExpenseReportService;
import com.artograd.api.services.IUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expensereports")
@AllArgsConstructor
public class ExpenseReportController {

  private IExpenseReportService expenseReportService;
  private IArtObjectService artObjectService;
  private IUserService userService;

  /**
   * Creates a new expense report.
   * This operation is allowed only for the supplier of the related art object.
   *
   * @param expenseReport The expense report to be created
   * @param request The HTTP request
   * @return A ResponseEntity representing the status of the creation operation -HttpStatus.CREATED
   *     if the expense report is successfully created -
   *     HttpStatus.FORBIDDEN if the user is not the supplier
   */
  @PostMapping
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<ExpenseReport> createExpenseReport(
      @RequestBody ExpenseReport expenseReport, HttpServletRequest request) {
    Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);

    return claims
        .flatMap(c -> artObjectService.getArtObject(expenseReport.getArtObjectId()))
        .filter(artObject -> artObject.getSupplier() != null)
        .filter(artObject -> artObject.getSupplier().getId().equals(claims.get().getUsername()))
        .map(artObject -> {
          ExpenseReport createdExpenseReport = 
                expenseReportService.createExpenseReport(expenseReport);
          return ResponseEntity.status(HttpStatus.CREATED).body(createdExpenseReport);
        })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
  }

  /**
   * Retrieves the expense report with the specified ID. 
   * This operation is allowed for both the supplier and owner of the related art object.
   *
   * @param id The ID of the expense report to retrieve
   * @param request The HTTP request
   * @return A ResponseEntity object representing the status of the retrieval operation: -
   *     ResponseEntity.ok() with the body set to the retrieved expense report if the object exists
   *     ResponseEntity.notFound() if the object does not exist or user is not authorized
   */
  @GetMapping("/{id}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<ExpenseReport> getExpenseReportById(@PathVariable String id,
      HttpServletRequest request) {
    Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);

    return expenseReportService.getExpenseReportById(id)
        .flatMap(expenseReport -> artObjectService.getArtObject(expenseReport.getArtObjectId())
            .filter(artObject -> artObject.getSupplier().getId().equals(claims.get().getUsername())
                || artObject.getOwner().getId().equals(claims.get().getUsername()))
            .map(artObject -> ResponseEntity.ok().body(expenseReport))
        )
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * Deletes the expense report with the specified ID. 
   * This operation is allowed only for the supplier of the related art object.
   *
   * @param id The ID of the expense report to delete
   * @param request The HTTP request
   * @return A ResponseEntity representing the status of the delete operation: -
   *     ResponseEntity.noContent() if the expense report is successfully deleted -
   *     ResponseEntity.status(HttpStatus.FORBIDDEN) if the user is not the supplier
   */
  @DeleteMapping("/{id}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<?> deleteExpenseReportById(@PathVariable String id, 
      HttpServletRequest request) {
    Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);

    return expenseReportService.getExpenseReportById(id)
        .flatMap(expenseReport -> artObjectService.getArtObject(expenseReport.getArtObjectId()))
        .filter(artObject -> artObject.getSupplier() != null)
        .filter(artObject -> artObject.getSupplier().getId().equals(claims.get().getUsername()))
        .map(expenseReport -> {
          expenseReportService.deleteExpenseReportById(id);
          return ResponseEntity.noContent().build();
        })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
  }

  /**
   * Retrieves the list of expense reports for the given art object, ordered by date descending.
   * This operation is allowed only for the owner or supplier of the related art object.
   *
   * @param artObjectId The ID of the art object whose expense reports are to be retrieved
   * @param request The HTTP request
   * @return A ResponseEntity object containing a list of matching expense reports, 
   *       or status FORBIDDEN if not authorized
   */
  @GetMapping("/artobject/{artObjectId}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<List<ExpenseReport>> getExpenseReportsByArtObjectId(
      @PathVariable String artObjectId, HttpServletRequest request) {
    Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);

    return claims
        .flatMap(c -> artObjectService.getArtObject(artObjectId))
        .filter(artObject -> 
            artObject.getSupplier().getId().equals(claims.get().getUsername())
            || artObject.getOwner().getId().equals(claims.get().getUsername()))
        .map(artObject -> {
          List<ExpenseReport> expenseReports = 
                expenseReportService.getExpenseReportsByArtObjectId(artObjectId);
          return ResponseEntity.ok().body(expenseReports);
        })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
  }
}
