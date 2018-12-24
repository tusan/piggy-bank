package com.piggybank.repository.dataloader;

import com.piggybank.model.Expense;
import com.piggybank.model.ExpenseType;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
class CsvDataLoader implements DataLoader {

  public List<Expense> load() {
    final Reader reader = getReader();
    try {
      return CSVParser.parse(reader, CSVFormat.DEFAULT
          .withHeader(CsvHeader.class)
          .withFirstRecordAsHeader())
          .getRecords()
          .stream()
          .map(getCsvRecordExpenseFunction())
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Reader getReader() {
    try {
      return new InputStreamReader(getClass().getResourceAsStream("/expenses.csv"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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

  private String getStringValue(CSVRecord record, CsvHeader header) {
    return Strings.trimToNull(record.get(header));
  }

  enum CsvHeader {
    TYPE, AMOUNT, DATE, DESCRIPTION
  }
}
