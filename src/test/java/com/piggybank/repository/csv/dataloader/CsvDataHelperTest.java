package com.piggybank.repository.csv.dataloader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.piggybank.model.Expense;
import com.piggybank.model.ExpenseType;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CsvDataHelperTest {

  private CsvDataHelper sut;
  private static final Path TEST_FILE = Paths.get("testCsvFile.csv");

  @After
  public void tearDown() {
    try {
      if (Files.exists(TEST_FILE)) {
        Files.delete(TEST_FILE);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  @Before
  public void setUp() {
    sut = new CsvDataHelper();
    sut.setCsvFile(TEST_FILE.toString());

    initializeCsvFile();
  }


  @Test
  public void shouldLoadAllEntriesFromCsv() {
    final List<Expense> expected = Arrays.asList(Expense.newBuilder()
        .setDate(LocalDate.of(2018, Month.NOVEMBER, 29))
        .setDescription("test description 1")
        .setType(ExpenseType.CASA)
        .setAmount(12345)
        .build(),
      Expense.newBuilder()
        .setDate(LocalDate.of(2018, Month.DECEMBER, 29))
        .setDescription("test description 2")
        .setType(ExpenseType.MOTO)
        .setAmount(56789)
        .build());

    final List<Expense> result = sut.load();

    assertEquals(expected, result);
  }

  @Test
  public void shouldSaveAnEntryToCsv() {
    final Expense expenseToWrite = Expense.newBuilder()
      .setDate(LocalDate.of(2018, Month.JANUARY, 1))
      .setDescription("test saved description")
      .setType(ExpenseType.CASA)
      .setAmount(400)
      .build();

    sut.save(expenseToWrite);

    final List<Expense> result = sut.load();

    assertTrue(result.toString(), result.contains(expenseToWrite));
  }

  private static void initializeCsvFile() {
    try {
      final Writer fileWriter = Files.newBufferedWriter(TEST_FILE);
      fileWriter.append("DATE,AMOUNT,TYPE,DESCRIPTION\n");
      fileWriter.append("20181129,12345.00,CASA,test description 1\n");
      fileWriter.append("20181229,56789.00,MOTO,test description 2\n");

      fileWriter.flush();
      fileWriter.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}