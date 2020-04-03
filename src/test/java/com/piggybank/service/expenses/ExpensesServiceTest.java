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
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        fakeExpense(ExpenseType.CASA, "description1", LocalDate.of(2018, Month.JANUARY, 1), owner);
    Expense februaryExpense =
        fakeExpense(ExpenseType.MOTO, "description2", LocalDate.of(2018, Month.FEBRUARY, 1), owner);
    Expense marchExpense =
        fakeExpense(
            ExpenseType.BOLLETTE, "description3", LocalDate.of(2018, Month.MARCH, 1), owner);

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
    final LocalDate dateStart = LocalDate.of(2018, Month.JANUARY, 1);
    final LocalDate dateEnd = dateStart.plusMonths(1);

    List<ExpenseDto> result =
        sut.find(getBuilder().setDateStart(dateStart).setDateEnd(dateEnd).build());

    assertEquals(
        Arrays.asList(
            fakeDtoExpense("description1", ExpenseType.CASA, LocalDate.of(2018, Month.JANUARY, 1)),
            fakeDtoExpense(
                "description2", ExpenseType.MOTO, LocalDate.of(2018, Month.FEBRUARY, 1))),
        result);
  }

  @Test
  public void shouldCallFilterByDateStartOnly() {

    List<ExpenseDto> result =
        sut.find(getBuilder().setDateStart(LocalDate.of(2018, Month.FEBRUARY, 1)).build());

    assertEquals(
        Arrays.asList(
            fakeDtoExpense("description2", ExpenseType.MOTO, LocalDate.of(2018, Month.FEBRUARY, 1)),
            fakeDtoExpense(
                "description3", ExpenseType.BOLLETTE, LocalDate.of(2018, Month.MARCH, 1))),
        result);
  }

  @Test
  public void shouldCallFilterByDateEndOnly() {

    List<ExpenseDto> result =
        sut.find(getBuilder().setDateEnd(LocalDate.of(2018, Month.FEBRUARY, 1)).build());

    assertEquals(
        Arrays.asList(
            fakeDtoExpense("description1", ExpenseType.CASA, LocalDate.of(2018, Month.JANUARY, 1)),
            fakeDtoExpense(
                "description2", ExpenseType.MOTO, LocalDate.of(2018, Month.FEBRUARY, 1))),
        result);
  }

  @Test
  public void shouldSaveTheExpense() {
    final LocalDate date = LocalDate.now();

    Optional<Long> actual = sut.save(ExpenseDto.newBuilder()
                .setAmount(123L)
                .setDescription("description")
                .setType(ExpenseType.CASA)
                .setDate(date)
                .build());


    assertTrue(actual.isPresent());
  }

  @Test
  public void shouldReturnOnlyExpensesAssociatedWithLoggedUser() {
    PiggyBankUser otherOwner = fakeUser("other_owner");
    testEntityManager.persist(otherOwner);

    Expense otherExpense =
        fakeExpense(ExpenseType.BOLLETTE, "test_description", LocalDate.now(), otherOwner);
    testEntityManager.persist(otherExpense);
    testEntityManager.flush();

    List<ExpenseDto> result = sut.find(getBuilder().build());
    assertEquals(
        Arrays.asList(
            fakeDtoExpense("description1", ExpenseType.CASA, LocalDate.of(2018, Month.JANUARY, 1)),
            fakeDtoExpense("description2", ExpenseType.MOTO, LocalDate.of(2018, Month.FEBRUARY, 1)),
            fakeDtoExpense(
                "description3", ExpenseType.BOLLETTE, LocalDate.of(2018, Month.MARCH, 1))),
        result);
  }

  private Expense fakeExpense(ExpenseType casa, String description, LocalDate date, PiggyBankUser owner) {
    Expense expense = new Expense();
    expense.setType(casa);
    expense.setDescription(description);
    expense.setDate(date);
    expense.setAmount(100);
    expense.setOwner(owner);
    return expense;
  }

  private PiggyBankUser fakeUser(String username) {
    final PiggyBankUser piggyBankUser = new PiggyBankUser();
    piggyBankUser.setPassword("password");
    piggyBankUser.setToken("token");
    piggyBankUser.setUsername(username);
    return piggyBankUser;
  }

  private ExpenseDto fakeDtoExpense(
      String description, ExpenseType expenseType, LocalDate date) {
    return ExpenseDto.newBuilder()
        .setAmount(100L)
        .setDescription(description)
        .setType(expenseType)
        .setDate(date)
        .build();
  }
}
