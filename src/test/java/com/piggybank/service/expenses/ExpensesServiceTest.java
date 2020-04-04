package com.piggybank.service.expenses;

import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import com.piggybank.service.expenses.dto.ExpenseDto;
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
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.piggybank.service.expenses.dto.ExpenseType.HOUSE;
import static com.piggybank.service.expenses.dto.ExpenseType.MOTORBIKE;
import static java.time.Month.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ExpensesServiceTest {
  @Autowired private TestEntityManager testEntityManager;

  @Autowired private JpaExpensesRepository repository;

  @MockBean private Principal loggedUser;

  private IExpensesService sut;

  private static IExpensesService.Query.Builder getBuilder() {
    return IExpensesService.Query.builder("test_user");
  }

  @Before
  public void setUp() {
    sut = new ExpensesService(repository);

    PiggyBankUser owner = fakeUser("test_user");
    testEntityManager.persist(owner);

    Expense januaryExpense =
        fakeExpense(HOUSE, "description1", LocalDate.of(2018, JANUARY, 1), owner);
    Expense februaryExpense =
        fakeExpense(MOTORBIKE, "description2", LocalDate.of(2018, FEBRUARY, 1), owner);
    Expense marchExpense =
        fakeExpense(
            ExpenseType.BILLS, "description3", LocalDate.of(2018, MARCH, 1), owner);

    testEntityManager.persist(januaryExpense);
    testEntityManager.persist(februaryExpense);
    testEntityManager.persist(marchExpense);

    testEntityManager.flush();
  }

  @After
  public void tearDown() {
    testEntityManager.clear();
    testEntityManager.flush();
  }

  @Test
  public void shouldCallFilterByDateStartAndDateEnd() {
    final LocalDate dateStart = LocalDate.of(2018, JANUARY, 1);
    final LocalDate dateEnd = dateStart.plusMonths(1);

    List<ExpenseDto> result =
        sut.find(getBuilder().setDateStart(dateStart).setDateEnd(dateEnd).build());

    assertEquals(
        Arrays.asList(
            fakeDtoExpense("description1", HOUSE, LocalDate.of(2018, JANUARY, 1)),
            fakeDtoExpense(
                "description2", MOTORBIKE, LocalDate.of(2018, FEBRUARY, 1))),
        result);
  }

  @Test
  public void shouldCallFilterByDateStartOnly() {

    List<ExpenseDto> result =
        sut.find(getBuilder().setDateStart(LocalDate.of(2018, FEBRUARY, 1)).build());

    assertEquals(
        Arrays.asList(
            fakeDtoExpense("description2", MOTORBIKE, LocalDate.of(2018, FEBRUARY, 1)),
            fakeDtoExpense(
                "description3", ExpenseType.BILLS, LocalDate.of(2018, MARCH, 1))),
        result);
  }

  @Test
  public void shouldCallFilterByDateEndOnly() {

    List<ExpenseDto> result =
        sut.find(getBuilder().setDateEnd(LocalDate.of(2018, FEBRUARY, 1)).build());

    assertEquals(
        Arrays.asList(
            fakeDtoExpense("description1", HOUSE, LocalDate.of(2018, JANUARY, 1)),
            fakeDtoExpense(
                "description2", MOTORBIKE, LocalDate.of(2018, FEBRUARY, 1))),
        result);
  }

  @Test
  public void shouldSaveTheExpense() {
    final LocalDate date = LocalDate.now();

    final Optional<Long> actual = sut.save(ExpenseDto.newBuilder()
                .setAmount(123L)
                .setDescription("description")
                .setType(HOUSE)
                .setDate(date)
                .build());


    assertTrue(actual.isPresent());
  }

  @Test
  public void shouldReturnOnlyExpensesAssociatedWithLoggedUser() {
    final PiggyBankUser otherOwner = fakeUser("other_owner");
    testEntityManager.persist(otherOwner);

    final Expense otherExpense =
        fakeExpense(ExpenseType.BILLS, "test_description", LocalDate.now(), otherOwner);
    testEntityManager.persist(otherExpense);
    testEntityManager.flush();

    final List<ExpenseDto> result = sut.find(getBuilder().build());
    assertEquals(
        Arrays.asList(
            fakeDtoExpense("description1", HOUSE, LocalDate.of(2018, JANUARY, 1)),
            fakeDtoExpense("description2", MOTORBIKE, LocalDate.of(2018, FEBRUARY, 1)),
            fakeDtoExpense(
                "description3", ExpenseType.BILLS, LocalDate.of(2018, MARCH, 1))),
        result);
  }

  private Expense fakeExpense(final ExpenseType expenseType,
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

  private PiggyBankUser fakeUser(final String username) {
    final PiggyBankUser piggyBankUser = new PiggyBankUser();
    piggyBankUser.setPassword("password");
    piggyBankUser.setToken("token");
    piggyBankUser.setUsername(username);
    return piggyBankUser;
  }

  private ExpenseDto fakeDtoExpense(final String description,
                                    final ExpenseType expenseType,
                                    final LocalDate date) {
    return ExpenseDto.newBuilder()
        .setAmount(100L)
        .setDescription(description)
        .setType(expenseType)
        .setDate(date)
        .build();
  }
}
