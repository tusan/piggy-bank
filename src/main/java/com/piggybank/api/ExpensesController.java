package com.piggybank.api;

import com.piggybank.service.expenses.IExpensesService;
import com.piggybank.service.expenses.dto.ExpenseDto;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequestMapping("api/v1/expenses")
@CrossOrigin
class ExpensesController {

  private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final IExpensesService expenseRepository;
  private final PrincipalProvider principalProvider;

  ExpensesController(
      final IExpensesService expenseRepository, final PrincipalProvider principalProvider) {
    this.expenseRepository = expenseRepository;
    this.principalProvider = principalProvider;
  }

  @GetMapping
  public ResponseEntity<List<ExpenseDto>> expenses(
      @RequestParam(value = "date-start", required = false) final String dateStart,
      @RequestParam(value = "date-end", required = false) final String dateEnd) {

    final LocalDate startDate =
        Strings.isBlank(dateStart) ? null : LocalDate.parse(dateStart, YYYY_MM_DD);

    final LocalDate endDate =
        Strings.isBlank(dateEnd) ? null : LocalDate.parse(dateEnd, YYYY_MM_DD);

    final List<ExpenseDto> result =
        expenseRepository.find(
            IExpensesService.Query.builder(principalProvider.getLoggedUser())
                .setDateStart(startDate)
                .setDateEnd(endDate)
                .build());

    return ResponseEntity.ok(result);
  }

  @PostMapping
  public ResponseEntity<Void> save(@RequestBody final ExpenseDto expenseDto) {
    try {
      expenseRepository.save(expenseDto);
      return ResponseEntity.status(HttpStatus.CREATED).build();
    } catch (final Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
