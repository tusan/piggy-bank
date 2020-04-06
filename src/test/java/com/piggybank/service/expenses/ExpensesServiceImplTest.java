package com.piggybank.service.expenses;

import com.piggybank.api.expenses.dto.ExpenseType;
import com.piggybank.service.authentication.repository.PiggyBankUser;
import com.piggybank.service.expenses.repository.Expense;
import com.piggybank.service.expenses.repository.JpaExpensesRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.piggybank.api.expenses.dto.ExpenseType.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExpensesServiceImplTest {
  private static final LocalDate JANUARY = LocalDate.of(2018, Month.JANUARY, 1);
  private static final LocalDate FEBRUARY = LocalDate.of(2018, Month.FEBRUARY, 1);
  private static final LocalDate MARCH = LocalDate.of(2018, Month.MARCH, 1);
  private static final PiggyBankUser LOGGED_USER = new PiggyBankUser();
  private static final Expense EXPENSE_JANUARY = fakeExpense(HOUSE, "description1", JANUARY);
  private static final Expense EXPENSE_FEBRUARY = fakeExpense(MOTORBIKE, "description2", FEBRUARY);
  private static final Expense EXPENSE_MARCH = fakeExpense(BILLS, "description3", MARCH);
  @Mock private JpaExpensesRepository repository;

  @InjectMocks private ExpensesServiceImpl sut;

  private static Expense fakeExpense(
      final ExpenseType expenseType, final String description, final LocalDate date) {
    final Expense expense = new Expense();

    expense.setId(1L);
    expense.setType(expenseType);
    expense.setDescription(description);
    expense.setDate(date);
    expense.setAmount(100);
    expense.setOwner(LOGGED_USER);

    return expense;
  }

  private static ExpensesService.Query.Builder baseQuery() {
    return ExpensesService.Query.builder(LOGGED_USER);
  }

  @Before
  public void setUp() {
    when(repository.findByOwner(LOGGED_USER))
        .thenReturn(Arrays.asList(EXPENSE_JANUARY, EXPENSE_FEBRUARY, EXPENSE_MARCH));

    when(repository.findByDateBetweenAndOwner(JANUARY, FEBRUARY, LOGGED_USER))
        .thenReturn(Arrays.asList(EXPENSE_JANUARY, EXPENSE_FEBRUARY));

    when(repository.findByDateGreaterThanEqualAndOwner(FEBRUARY, LOGGED_USER))
        .thenReturn(Arrays.asList(EXPENSE_FEBRUARY, EXPENSE_MARCH));

    when(repository.findByDateLessThanEqualAndOwner(FEBRUARY, LOGGED_USER))
        .thenReturn(Arrays.asList(EXPENSE_JANUARY, EXPENSE_FEBRUARY));

    when(repository.save(any())).then((Answer<Expense>) invocation -> invocation.getArgument(0));
  }

  @Test
  public void shouldReturnExpensesForGivenUser() {
    final List<Expense> result = sut.find(baseQuery().build());

    verify(repository).findByOwner(LOGGED_USER);
    assertEquals(asList(EXPENSE_JANUARY, EXPENSE_FEBRUARY, EXPENSE_MARCH), result);
  }

  @Test
  public void shouldReturnExpensesInDateRange() {
    final List<Expense> result =
        sut.find(baseQuery().setDateStart(JANUARY).setDateEnd(FEBRUARY).build());

    verify(repository).findByDateBetweenAndOwner(JANUARY, FEBRUARY, LOGGED_USER);
    assertEquals(asList(EXPENSE_JANUARY, EXPENSE_FEBRUARY), result);
  }

  @Test
  public void shouldReturnExpensesWithDateGreaterThenEqualGivenDate() {
    final List<Expense> result = sut.find(baseQuery().setDateStart(FEBRUARY).build());

    verify(repository).findByDateGreaterThanEqualAndOwner(FEBRUARY, LOGGED_USER);
    assertEquals(asList(EXPENSE_FEBRUARY, EXPENSE_MARCH), result);
  }

  @Test
  public void shouldReturnExpensesWithDateLessThenEqualGivenDate() {
    final List<Expense> result = sut.find(baseQuery().setDateEnd(FEBRUARY).build());

    verify(repository).findByDateLessThanEqualAndOwner(FEBRUARY, LOGGED_USER);
    assertEquals(asList(EXPENSE_JANUARY, EXPENSE_FEBRUARY), result);
  }

  @Test
  public void shouldSaveTheExpenseAndReturnTheId() {
    final Optional<Long> actual = sut.save(fakeExpense(HOUSE, "description", LocalDate.now()));

    assertTrue(actual.isPresent());
    actual.ifPresent(res -> assertEquals(Long.valueOf(1L), res));
  }
}
