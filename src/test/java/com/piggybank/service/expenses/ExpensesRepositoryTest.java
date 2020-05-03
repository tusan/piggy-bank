package com.piggybank.service.expenses;

import com.piggybank.api.expenses.ExpenseType;
import com.piggybank.service.users.PiggyBankUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static com.piggybank.api.expenses.ExpenseType.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ExpensesRepositoryTest {
  private static final LocalDate JANUARY = LocalDate.of(2018, Month.JANUARY, 1);
  private static final LocalDate FEBRUARY = LocalDate.of(2018, Month.FEBRUARY, 1);
  private static final LocalDate MARCH = LocalDate.of(2018, Month.MARCH, 1);
  public static final String DEFAULT_USER = "test_user";
  public static final String OTHER_OWNER = "other_owner";

  @Autowired private TestEntityManager testEntityManager;

  @Autowired private JpaExpensesRepository sut;

  @MockBean private Principal principal;

  private static Expense expenseJanuary() {
    return fakeExpense(HOUSE, "description1", JANUARY, createUser(DEFAULT_USER));
  }

  private static Expense expenseFebruary() {
    return fakeExpense(MOTORBIKE, "description2", FEBRUARY, createUser(DEFAULT_USER));
  }

  private static Expense expenseMarch() {
    return fakeExpense(BILLS, "description3", MARCH, createUser(DEFAULT_USER));
  }

  private static Expense fakeExpense(
          final ExpenseType expenseType,
          final String description,
          final LocalDate date,
          final PiggyBankUser owner) {
    final Expense expense = new Expense();

    expense.setType(expenseType);
    expense.setDescription(description);
    expense.setDate(date);
    expense.setAmount(100);
    expense.setOwner(owner);

    return expense;
  }

  @Before
  public void setUp() {
    final PiggyBankUser otherOwner = createUser(OTHER_OWNER);

    testEntityManager.persist(createUser(DEFAULT_USER));
    testEntityManager.flush();

    final Expense otherExpense =
        fakeExpense(BILLS, "test_description", LocalDate.now(), otherOwner);

    testEntityManager.persist(otherOwner);
    testEntityManager.persist(otherExpense);

    testEntityManager.persist(expenseJanuary());
    testEntityManager.persist(expenseFebruary());
    testEntityManager.persist(expenseMarch());

    testEntityManager.flush();
  }

  @After
  public void tearDown() {
    testEntityManager.clear();
    testEntityManager.flush();
  }

  @Test
  public void shouldCallFilterByDateStartAndDateEnd() {
    final List<Expense> result =
        sut.findByDateBetweenAndOwner(JANUARY, FEBRUARY, createUser(DEFAULT_USER));

    assertEquals(asList(expenseJanuary(), expenseFebruary()), result);
  }

  @Test
  public void shouldCallFilterByDateStartOnly() {
    final List<Expense> result =
        sut.findByDateGreaterThanEqualAndOwner(FEBRUARY, createUser(DEFAULT_USER));

    assertEquals(asList(expenseFebruary(), expenseMarch()), result);
  }

  @Test
  public void shouldCallFilterByDateEndOnly() {
    final List<Expense> result =
        sut.findByDateLessThanEqualAndOwner(FEBRUARY, createUser(DEFAULT_USER));

    assertEquals(asList(expenseJanuary(), expenseFebruary()), result);
  }

  @Test
  public void shouldSaveTheExpense() {
    assertNotNull(
        sut.save(fakeExpense(HOUSE, "description", LocalDate.now(), createUser(DEFAULT_USER))));
  }

  @Test
  public void shouldReturnOnlyExpensesAssociatedWithLoggedUser() {
    final List<Expense> result = sut.findByOwner(createUser(DEFAULT_USER));
    assertEquals(asList(expenseJanuary(), expenseFebruary(), expenseMarch()), result);
  }

  private static PiggyBankUser createUser(final String username) {
    return PiggyBankUser.forUsernamePasswordAndToken(username, "password", "token");
  }
}
