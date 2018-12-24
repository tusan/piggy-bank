package com.piggybank.repository.dataloader;

import static org.junit.Assert.*;

import com.piggybank.model.Expense;
import com.piggybank.model.ExpenseType;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CsvDataLoaderTest {

  private CsvDataLoader sut;

  @Before
  public void setUp() {
    sut = new CsvDataLoader();
  }

  @Test
  @Ignore
  public void shouldLoadAllEntriesFromCsv() {
    List<Expense> expected = Arrays.asList(Expense.newBuilder()
            .setDate(LocalDate.of(2018, Month.FEBRUARY, 26))
            .setDescription("Tagliando")
            .setType(ExpenseType.MOTO)
            .setAmount(200)
            .build(),
        Expense.newBuilder()
            .setDate(LocalDate.of(2018, Month.JANUARY, 1))
            .setDescription("Affitto")
            .setType(ExpenseType.CASA)
            .setAmount(400)
            .build());

    List<Expense> result = sut.load();

    assertEquals(expected, result);
  }
}