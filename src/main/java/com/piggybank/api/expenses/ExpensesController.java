package com.piggybank.api.expenses;

import com.piggybank.service.expenses.ExpensesService;
import com.piggybank.service.expenses.repository.Expense;
import com.piggybank.service.users.repository.PiggyBankUser;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.piggybank.api.expenses.ExpenseConverter.toEntity;
import static com.piggybank.config.Environment.DATE_TIME_FORMATTER;
import static com.piggybank.config.Environment.INPUT_DATE_FORMAT;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;
import static java.util.Collections.emptyList;
import static java.util.logging.Level.SEVERE;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("api/v1/expenses")
@SecurityScheme(name = "bearerToken", type = HTTP, scheme = "bearer", bearerFormat = "JWT")
@CrossOrigin
class ExpensesController {
  private static final Logger LOGGER = Logger.getLogger(ExpensesController.class.getName());

  private final ExpensesService expenseRepository;

  ExpensesController(final ExpensesService expenseRepository) {
    this.expenseRepository = expenseRepository;
  }

  @GetMapping
  @SecurityRequirement(name = "bearerToken")
  public ResponseEntity<List<ExpenseDto>> expenses(
      @Parameter(description = INPUT_DATE_FORMAT)
          @RequestParam(value = "date-start", required = false)
          final String dateStart,
      @Parameter(description = INPUT_DATE_FORMAT)
          @RequestParam(value = "date-end", required = false)
          final String dateEnd,
      @Parameter(hidden = true) final Authentication principal) {

    return resolveUser(principal)
        .map(buildQueryObject(dateStart, dateEnd))
        .map(this::associatedExpenses)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ok(emptyList()));
  }

  @PostMapping
  @SecurityRequirement(name = "bearerToken")
  public ResponseEntity<Void> save(
      @RequestBody final ExpenseDto expenseDto,
      @Parameter(hidden = true) final Authentication principal) {
    return resolveUser(principal)
        .map(owner -> toEntity(expenseDto, owner))
        .flatMap(trySave())
        .orElseGet(() -> status(INTERNAL_SERVER_ERROR).build());
  }

  private Function<Expense, Optional<ResponseEntity<Void>>> trySave() {
    return entity -> {
      try {
        expenseRepository.save(entity);
        return Optional.of(status(CREATED).build());
      } catch (final Exception e) {
        LOGGER.log(SEVERE, String.format("Error while saving entity [entity=%s]", entity), e);
        return Optional.empty();
      }
    };
  }

  private Function<PiggyBankUser, ExpensesService.Query> buildQueryObject(
      final String dateStart, final String dateEnd) {

    final LocalDate startDate =
        isBlank(dateStart) ? null : LocalDate.parse(dateStart, DATE_TIME_FORMATTER);

    final LocalDate endDate =
        isBlank(dateEnd) ? null : LocalDate.parse(dateEnd, DATE_TIME_FORMATTER);

    return owner ->
        ExpensesService.Query.builder(owner).setDateStart(startDate).setDateEnd(endDate).build();
  }

  private List<ExpenseDto> associatedExpenses(final ExpensesService.Query query) {
    return expenseRepository.find(query).stream()
        .map(ExpenseConverter::toDto)
        .collect(Collectors.toList());
  }

  private Optional<PiggyBankUser> resolveUser(final Principal principal) {
    final Authentication authenticationToken = (Authentication) principal;
    final PiggyBankUser piggyBankUser = (PiggyBankUser) authenticationToken.getPrincipal();

    return Optional.ofNullable(piggyBankUser);
  }
}
