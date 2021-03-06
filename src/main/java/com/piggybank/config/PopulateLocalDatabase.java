package com.piggybank.config;

import com.piggybank.service.expenses.ExpensesService;
import com.piggybank.service.expenses.repository.Expense;
import com.piggybank.service.users.AuthenticationService;
import com.piggybank.service.users.repository.PiggyBankUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;
import java.util.logging.Logger;

import static com.piggybank.api.expenses.ExpenseType.BANK_ACCOUNT;
import static com.piggybank.service.users.repository.PiggyBankUser.forUsernameAndPassword;

@Profile("default")
@ConditionalOnProperty(name = "piggy_bank.features.populate_db_with_dummy_data")
@Component
public class PopulateLocalDatabase implements ApplicationListener<ApplicationReadyEvent> {
  private static final Logger LOGGER = Logger.getLogger(PopulateLocalDatabase.class.getName());

  @Autowired private AuthenticationService authenticationService;
  @Autowired private ExpensesService expensesService;

  private static Expense singleExpense(
      final PiggyBankUser owner, final long id, final double amount) {
    final Expense expense = new Expense();
    expense.setOwner(owner);
    expense.setId(id);
    expense.setAmount(amount);
    expense.setDate(LocalDate.now());
    expense.setType(BANK_ACCOUNT);

    return expense;
  }

  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {
    LOGGER.info("START POPULATING DATABASE");

    final PiggyBankUser user = forUsernameAndPassword("username", "password");
    authenticationService.add(user);

    new Random()
        .ints(0, 100)
        .mapToObj(i -> singleExpense(user, i, 1000d / i * 100))
        .limit(10)
        .forEach(expensesService::save);

    LOGGER.info("FINISHED POPULATING DATABASE");
  }
}
