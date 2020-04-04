package com.piggybank.service.expenses;

import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import com.piggybank.service.expenses.dto.ExpenseType;
import com.piggybank.service.expenses.repository.Expense;
import com.piggybank.service.expenses.repository.JpaExpensesRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static com.piggybank.service.expenses.dto.ExpenseType.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ExpensesServiceImplTest {
  private static final LocalDate JANUARY = LocalDate.of(2018, Month.JANUARY, 1);
  private static final LocalDate FEBRUARY = LocalDate.of(2018, Month.FEBRUARY, 1);
  private static final LocalDate MARCH = LocalDate.of(2018, Month.MARCH, 1);

  @Autowired private TestEntityManager testEntityManager;

  @Autowired private JpaExpensesRepository repository;

  @MockBean private Principal principal;

  private ExpensesService sut;

  @Before
  public void setUp() {
    sut = new ExpensesServiceImpl(repository);

    final PiggyBankUser otherOwner = fakeUser("other_owner");

    testEntityManager.persist(loggedUser());
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

    List<Expense> result = sut.find(baseQuery().setDateStart(JANUARY).setDateEnd(FEBRUARY).build());

    assertEquals(asList(expenseJanuary(), expenseFebruary()), result);
  }

  @Test
  public void shouldCallFilterByDateStartOnly() {
    List<Expense> result = sut.find(baseQuery().setDateStart(FEBRUARY).build());

    assertEquals(asList(expenseFebruary(), expenseMarch()), result);
  }

  @Test
  public void shouldCallFilterByDateEndOnly() {
    final List<Expense> result = sut.find(baseQuery().setDateEnd(FEBRUARY).build());

    assertEquals(asList(expenseJanuary(), expenseFebruary()), result);
  }

  @Test
  public void shouldSaveTheExpense() {
    final Optional<Long> actual =
        sut.save(fakeExpense(HOUSE, "description", LocalDate.now(), loggedUser()));

    assertTrue(actual.isPresent());
  }

  @Test
  public void shouldReturnOnlyExpensesAssociatedWithLoggedUser() {
    final List<Expense> result = sut.find(baseQuery().build());
    assertEquals(asList(expenseJanuary(), expenseFebruary(), expenseMarch()), result);
  }

  private static PiggyBankUser fakeUser(final String username) {
    final PiggyBankUser piggyBankUser = new PiggyBankUser();
    piggyBankUser.setPassword("password");
    piggyBankUser.setToken("token");
    piggyBankUser.setUsername(username);
    return piggyBankUser;
  }

  private static Expense expenseJanuary() {
    return fakeExpense(HOUSE, "description1", JANUARY, loggedUser());
  }

  private static Expense expenseFebruary() {
    return fakeExpense(MOTORBIKE, "description2", FEBRUARY, loggedUser());
  }

  private static Expense expenseMarch() {
    return fakeExpense(BILLS, "description3", MARCH, loggedUser());
  }

  private static ExpensesService.Query.Builder baseQuery() {
    return ExpensesService.Query.builder("test_user");
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

  private static PiggyBankUser loggedUser() {
    return fakeUser("test_user");
  }
}
