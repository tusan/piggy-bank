package com.piggybank.config;

import com.piggybank.service.authentication.AuthenticationService;
import com.piggybank.service.authentication.repository.PiggyBankUser;
import com.piggybank.service.expenses.ExpensesService;
import com.piggybank.service.expenses.repository.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;
import java.util.logging.Logger;

import static com.piggybank.api.expenses.dto.ExpenseType.BANK_ACCOUNT;

@Profile("default")
@ConditionalOnProperty(name = "piggy_bank.features.populate_db_with_dummy_data")
@Component
public class PopulateLocalDatabase implements ApplicationListener<ApplicationReadyEvent> {
  private static final Logger LOGGER = Logger.getLogger(PopulateLocalDatabase.class.getName());

  @Autowired private AuthenticationService authenticationService;
  @Autowired private ExpensesService expensesService;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    LOGGER.info("START POPULATING DATABASE");

    addExpenses(createUser(authenticationService));

    LOGGER.info("FINISHED POPULATING DATABASE");
  }

  private void addExpenses(PiggyBankUser owner) {
    new Random()
        .ints(0, 100)
        .mapToObj(i -> singleExpense(owner, i, 1000d / i * 100))
        .limit(10)
        .forEach(expensesService::save);
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

  private static PiggyBankUser createUser(final AuthenticationService authenticationService) {
    final PiggyBankUser user = new PiggyBankUser();
    user.setUsername("username");
    user.setPassword("password");

    authenticationService.add(user);
    return user;
  }
}
