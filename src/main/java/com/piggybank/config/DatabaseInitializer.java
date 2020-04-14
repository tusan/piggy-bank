package com.piggybank.config;

import com.piggybank.api.expenses.dto.ExpenseType;
import com.piggybank.service.authentication.AuthenticationService;
import com.piggybank.service.authentication.repository.PiggyBankUser;
import com.piggybank.service.expenses.ExpensesService;
import com.piggybank.service.expenses.repository.Expense;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;
import java.util.logging.Logger;

@Component
@Profile("default")
public class DatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {
  private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

  private final AuthenticationService authenticationService;
  private final ExpensesService expensesService;

  public DatabaseInitializer(
      final AuthenticationService authenticationService, final ExpensesService expensesService) {
    this.authenticationService = authenticationService;
    this.expensesService = expensesService;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    LOGGER.info("START POPULATING DATABASE");

    Random r = new Random();
    final PiggyBankUser user = new PiggyBankUser();
    user.setUsername("username");
    user.setPassword("password");

    authenticationService.add(user);

    for (long i = 0; i < 10; i++) {
      final Expense expense = new Expense();
      expense.setOwner(user);
      expense.setId(i);
      expense.setAmount(r.nextInt(1000));
      expense.setDate(LocalDate.now());
      expense.setType(ExpenseType.BANK_ACCOUNT);

      expensesService.save(expense);
    }

    LOGGER.info("FINISHED POPULATING DATABASE");
  }
}
