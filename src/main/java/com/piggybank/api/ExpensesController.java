package com.piggybank.api;

import com.piggybank.service.expenses.ExpensesService;
import com.piggybank.service.expenses.dto.ExpenseDto;
import com.piggybank.service.expenses.repository.Expense;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("api/v1/expenses")
@CrossOrigin
class ExpensesController {

  private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final ExpensesService expenseRepository;
  private final PrincipalProvider principalProvider;

  ExpensesController(
      final ExpensesService expenseRepository, final PrincipalProvider principalProvider) {
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
        expenseRepository
            .find(
                ExpensesService.Query.builder(principalProvider.getLoggedUser())
                    .setDateStart(startDate)
                    .setDateEnd(endDate)
                    .build())
            .stream()
            .map(ExpenseConverter::toDto)
            .collect(Collectors.toList());

    return ResponseEntity.ok(result);
  }

  @PostMapping
  public ResponseEntity<Void> save(@RequestBody final ExpenseDto expenseDto) {
    try {
      expenseRepository.save(ExpenseConverter.toEntity(expenseDto));
      return ResponseEntity.status(HttpStatus.CREATED).build();
    } catch (final Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  static final class ExpenseConverter {
    static Expense toEntity(final ExpenseDto expenseDto) {
      final Expense exp = new Expense();

      exp.setType(expenseDto.type());
      exp.setDate(expenseDto.date());
      exp.setAmount(expenseDto.amount());
      exp.setDescription(expenseDto.description());

      return exp;
    }

    static ExpenseDto toDto(final Expense exp) {
      return ExpenseDto.newBuilder()
          .setAmount(exp.getAmount())
          .setDate(exp.getDate())
          .setDescription(exp.getDescription())
          .setType(exp.getType())
          .build();
    }
  }
}
