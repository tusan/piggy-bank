package com.piggybank.config;

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

import static com.piggybank.api.expenses.dto.ExpenseType.BANK_ACCOUNT;

class DatabaseInitializers {
  private static final Logger LOGGER = Logger.getLogger(DatabaseInitializers.class.getName());
  private static final Random RANDOM_GENERATOR = new Random();

  @Profile("default")
  @Component
  static class LocalEnvironmentInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final AuthenticationService authenticationService;
    private final ExpensesService expensesService;

    public LocalEnvironmentInitializer(
        final AuthenticationService authenticationService, final ExpensesService expensesService) {
      this.authenticationService = authenticationService;
      this.expensesService = expensesService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
      LOGGER.info("START POPULATING DATABASE");

      final PiggyBankUser owner = createUser(authenticationService);

      RANDOM_GENERATOR
          .ints(0, 100)
          .mapToObj(i -> singleExpense(owner, i, 1000d / i * 100))
          .limit(10)
          .forEach(expensesService::save);

      LOGGER.info("FINISHED POPULATING DATABASE");
    }

    private static Expense singleExpense(PiggyBankUser owner, long id, double amount) {
      final Expense expense = new Expense();
      expense.setOwner(owner);
      expense.setId(id);
      expense.setAmount(amount);
      expense.setDate(LocalDate.now());
      expense.setType(BANK_ACCOUNT);

      return expense;
    }
  }

  @Component
  @Profile("integration")
  private static class IntegrationTestsInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final AuthenticationService authenticationService;

    private IntegrationTestsInitializer(
        AuthenticationService authenticationService) {
      this.authenticationService = authenticationService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
      LOGGER.info("START POPULATING DATABASE");

      final PiggyBankUser user = createUser(authenticationService);

      LOGGER.info("FINISHED POPULATING DATABASE");
    }
  }

  private static PiggyBankUser createUser(final AuthenticationService authenticationService) {
    final PiggyBankUser user = new PiggyBankUser();
    user.setUsername("username");
    user.setPassword("password");

    authenticationService.add(user);
    return user;
  }
}
