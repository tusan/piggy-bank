package com.piggybank.repository.csv;

import static org.junit.Assert.assertEquals;

import com.piggybank.model.Expense;
import com.piggybank.model.ExpenseType;
import com.piggybank.repository.ExpenseQuery;
import com.piggybank.repository.csv.dataloader.DataLoader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CsvExpenseRepositoryTest {

  @InjectMocks
  private CsvExpenseRepository sut;

  @Mock
  private DataLoader dataLoader;

  @Before
  public void setUp() {
    Mockito.when(dataLoader.load()).thenReturn(Arrays.asList(
        Expense.newBuilder()
            .setType(ExpenseType.MOTO)
            .setDate(LocalDate.of(2018, Month.FEBRUARY, 26))
            .setDescription("Tagliando")
            .setAmount(200)
            .build(),
        Expense.newBuilder()
            .setType(ExpenseType.CASA)
          .setDate(LocalDate.of(2018, Month.JANUARY, 1))
            .setDescription("Affitto")
            .setAmount(400)
            .build()));
  }

  @Test
  public void shouldFilterByStartDate() {
    final List<Expense> expected = Collections.singletonList(Expense.newBuilder()
        .setDate(LocalDate.of(2018, Month.FEBRUARY, 26))
        .setDescription("Tagliando")
        .setType(ExpenseType.MOTO)
        .setAmount(200)
        .build());

    final List<Expense> result = sut.find(baseQueryBuilder()
        .setDateStart(LocalDate.of(2018, Month.JANUARY, 2))
        .build());

    assertEquals(expected, result);
  }

  @Test
  public void shouldFilterByEndDate() {
    final List<Expense> expected = Collections.singletonList(Expense.newBuilder()
        .setDate(LocalDate.of(2018, Month.JANUARY, 1))
        .setDescription("Affitto")
        .setType(ExpenseType.CASA)
        .setAmount(400)
        .build());

    final List<Expense> result = sut.find(baseQueryBuilder()
        .setDateEnd(LocalDate.of(2018, Month.JANUARY, 1))
        .build());

    assertEquals(expected, result);
  }

  @Test
  public void shouldFilterByEndDateIncludingDateFilter() {
    final List<Expense> expected = Collections.singletonList(Expense.newBuilder()
        .setDate(LocalDate.of(2018, Month.JANUARY, 1))
        .setDescription("Affitto")
        .setType(ExpenseType.CASA)
        .setAmount(400)
        .build());

    final List<Expense> result = sut.find(baseQueryBuilder()
        .setDateEnd(LocalDate.of(2018, Month.FEBRUARY, 25))
        .build());

    assertEquals(expected, result);
  }

  private ExpenseQuery.Builder baseQueryBuilder() {
    return ExpenseQuery.builder()
        .setDateEnd(LocalDate.now())
        .setDateStart(LocalDateTime.MIN.toLocalDate());
  }
}