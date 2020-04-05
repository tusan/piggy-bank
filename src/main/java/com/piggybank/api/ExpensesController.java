package com.piggybank.api;

import com.piggybank.security.AuthenticationResolver;
import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import com.piggybank.service.expenses.ExpensesService;
import com.piggybank.service.expenses.dto.ExpenseDto;
import com.piggybank.service.expenses.repository.Expense;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.piggybank.api.ExpensesController.ExpenseConverter.toEntity;
import static com.piggybank.config.Environment.DATE_TIME_FORMATTER;
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
          final String dateEnd,
      final Authentication principal) {

    return authenticationResolver
        .retrieveForToken(String.valueOf(principal.getCredentials()))
        .map(buildQueryObject(dateStart, dateEnd))
        .map(this::associatedExpenses)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ok(emptyList()));
  }

  @PostMapping
  @SecurityRequirement(name = "bearerToken")
  public ResponseEntity<Void> save(
      @RequestBody final ExpenseDto expenseDto, final Authentication principal) {
    return authenticationResolver
        .retrieveForToken(principal.getCredentials().toString())
        .map(owner -> toEntity(expenseDto, owner))
        .flatMap(trySave())
        .orElseGet(() -> status(INTERNAL_SERVER_ERROR).build());
  }

  private Function<Expense, Optional<ResponseEntity<Void>>> trySave() {
    return entity -> {
      try {
        expenseRepository.save(entity);
        return Optional.of(status(CREATED).build());
      } catch (Exception e) {
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

  private List<ExpenseDto> associatedExpenses(ExpensesService.Query query) {
    return expenseRepository.find(query).stream()
        .map(ExpenseConverter::toDto)
        .collect(Collectors.toList());
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
