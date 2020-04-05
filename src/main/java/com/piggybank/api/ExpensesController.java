package com.piggybank.api;

import com.piggybank.security.AuthenticationResolver;
import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import com.piggybank.service.expenses.ExpensesService;
import com.piggybank.service.expenses.dto.ExpenseDto;
import com.piggybank.service.expenses.repository.Expense;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.piggybank.api.ExpensesController.ExpenseConverter.toEntity;
import static com.piggybank.config.Environment.DATE_TIME_FORMATTER;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;
import static org.apache.logging.log4j.util.Strings.isBlank;

@RestController
@RequestMapping("api/v1/expenses")
@SecurityScheme(name = "bearerToken", type = HTTP, scheme = "bearer", bearerFormat = "JWT")
@CrossOrigin
class ExpensesController {

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
      @Parameter(example = "2020-03-27") @RequestParam(value = "date-start", required = false)
          final String dateStart,
      @Parameter(example = "2020-03-30") @RequestParam(value = "date-end", required = false)
          final String dateEnd) {

    final LocalDate startDate =
        isBlank(dateStart) ? null : LocalDate.parse(dateStart, DATE_TIME_FORMATTER);

    final LocalDate endDate =
        isBlank(dateEnd) ? null : LocalDate.parse(dateEnd, DATE_TIME_FORMATTER);

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
      final PiggyBankUser owner = authenticationResolver.getLoggedUser();
      expenseRepository.save(toEntity(expenseDto, owner));
      return ResponseEntity.status(HttpStatus.CREATED).build();
    } catch (final Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  static final class ExpenseConverter {
    static Expense toEntity(final ExpenseDto expenseDto, final PiggyBankUser owner) {
      final Expense exp = new Expense();

      exp.setType(expenseDto.type());
      exp.setDate(expenseDto.date());
      exp.setAmount(expenseDto.amount());
      exp.setDescription(expenseDto.description());
      exp.setOwner(owner);

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
