package com.piggybank.api;

import com.piggybank.security.AuthenticationResolver;
import com.piggybank.service.expenses.ExpensesService;
import com.piggybank.service.expenses.dto.ExpenseDto;
import com.piggybank.service.expenses.repository.Expense;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/expenses")
@SecurityScheme(
    name = "bearerToken",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT")
@CrossOrigin
class ExpensesController {

  private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final ExpensesService expenseRepository;
  private final AuthenticationResolver authenticationResolver;

  ExpensesController(
      final ExpensesService expenseRepository,
      final AuthenticationResolver authenticationResolver) {
    this.expenseRepository = expenseRepository;
    this.authenticationResolver = authenticationResolver;
  }

  @GetMapping
  @SecurityRequirement(name = "bearerToken")
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
                ExpensesService.Query.builder(authenticationResolver.getLoggedUser())
                    .setDateStart(startDate)
                    .setDateEnd(endDate)
                    .build())
            .stream()
            .map(ExpenseConverter::toDto)
            .collect(Collectors.toList());

    return ResponseEntity.ok(result);
  }

  @PostMapping
  @SecurityRequirement(name = "bearerToken")
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
