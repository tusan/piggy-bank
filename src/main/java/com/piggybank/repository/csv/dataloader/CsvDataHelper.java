package com.piggybank.repository.csv.dataloader;

import com.piggybank.model.Expense;
import com.piggybank.model.ExpenseType;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class CsvDataHelper implements DataLoader {

  @Value("${expenses.storage.file}")
  private String csvFile;

  @Override
  public List<Expense> load() {
    final Reader reader;
    try {
      reader = Files.newBufferedReader(getCsvFilePath());
    } catch (final Exception e) {
      return Collections.emptyList();
    }

    try {
      return CSVParser.parse(reader, CSVFormat.DEFAULT
        .withHeader(CsvHeader.class)
        .withFirstRecordAsHeader())
        .getRecords()
        .stream()
        .map(getCsvRecordExpenseFunction())
        .collect(Collectors.toList());
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void save(final Expense expense) {
    final Path csvFilePath = getCsvFilePath();
    try {
      final BufferedWriter writer = Files.newBufferedWriter(csvFilePath,
        StandardOpenOption.APPEND,
        StandardOpenOption.CREATE);

      final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
        .withHeader(CsvHeader.class)
        .withFirstRecordAsHeader());

      if (Files.size(csvFilePath) == 0) {
        csvPrinter.printRecord("DATE", "AMOUNT", "TYPE", "DESCRIPTION");
      }

      csvPrinter
        .printRecord(expense.date().format(DateTimeFormatter
            .ofPattern("yyyyMMdd")),
          expense.amount(),
          expense.type(),
          expense.description());

      csvPrinter.flush();
      csvPrinter.close();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  void setCsvFile(final String csvFile) {
    this.csvFile = csvFile;
  }

  private Path getCsvFilePath() {
    return Paths.get(csvFile);
  }

  private Function<CSVRecord, Expense> getCsvRecordExpenseFunction() {
    return record -> {
      final String expenseCategory = getStringValue(record, CsvHeader.TYPE);
      final String amount = getStringValue(record, CsvHeader.AMOUNT);
      final String description = getStringValue(record, CsvHeader.DESCRIPTION);
      final LocalDate date = LocalDate
        .parse(getStringValue(record, CsvHeader.DATE), DateTimeFormatter
          .ofPattern("yyyyMMdd"));

      return Expense.newBuilder()
        .setType(ExpenseType.valueOf(expenseCategory))
        .setAmount(Double.parseDouble(amount))
        .setDate(date)
        .setDescription(description)
        .build();
    };
  }

  private String getStringValue(final CSVRecord record, final CsvHeader header) {
    return Strings.trimToNull(record.get(header));
  }

  enum CsvHeader {
    TYPE, AMOUNT, DATE, DESCRIPTION
  }
}
