package com.piggybank.repository;

import static org.junit.Assert.assertEquals;

import com.piggybank.model.Expense;
import com.piggybank.model.ExpenseType;
import com.piggybank.repository.dataloader.DataLoader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
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
            .setDate(LocalDate.parse("20180226", DateTimeFormatter
                .ofPattern("yyyyMMdd")))
            .setDescription("Tagliando")
            .setAmount(200)
            .build(),
        Expense.newBuilder()
            .setType(ExpenseType.CASA)
            .setDate(LocalDate.parse("20180101",DateTimeFormatter
                .ofPattern("yyyyMMdd")))
            .setDescription("Affitto")
            .setAmount(400)
            .build()));
  }

  @Test
  public void shouldFilterByCategory() {
    List<Expense> expected = Collections.singletonList(Expense.newBuilder()
        .setDate(LocalDate.of(2018, Month.FEBRUARY, 26))
        .setDescription("Tagliando")
        .setType(ExpenseType.MOTO)
        .setAmount(200)
        .build());

    List<Expense> result = sut.find(baseQueryBuilder()
        .setCategory(ExpenseType.MOTO)
        .build());

    assertEquals(expected, result);
  }

  @Test
  public void shouldFilterByStartDate() {
    List<Expense> expected = Collections.singletonList(Expense.newBuilder()
        .setDate(LocalDate.of(2018, Month.FEBRUARY, 26))
        .setDescription("Tagliando")
        .setType(ExpenseType.MOTO)
        .setAmount(200)
        .build());

    List<Expense> result = sut.find(baseQueryBuilder()
        .setDateStart(LocalDate.of(2018, Month.JANUARY, 2))
        .build());

    assertEquals(expected, result);
  }

  @Test
  public void shouldFilterByEndDate() {
    List<Expense> expected = Collections.singletonList(Expense.newBuilder()
        .setDate(LocalDate.of(2018, Month.JANUARY, 1))
        .setDescription("Affitto")
        .setType(ExpenseType.CASA)
        .setAmount(400)
        .build());

    List<Expense> result = sut.find(baseQueryBuilder()
        .setDateEnd(LocalDate.of(2018, Month.FEBRUARY, 25))
        .build());

    assertEquals(expected, result);
  }

  private ExpenseQuery.Builder baseQueryBuilder() {
    return ExpenseQuery.builder()
        .setCategory(ExpenseType.ALL)
        .setDateEnd(LocalDate.now())
        .setDateStart(LocalDateTime.MIN.toLocalDate());
  }
}