package com.piggybank.repository.csv;

import com.piggybank.model.Expense;
import com.piggybank.model.ExpenseType;
import com.piggybank.repository.ExpenseQuery;
import com.piggybank.repository.ExpenseRepository;
import com.piggybank.repository.csv.dataloader.DataLoader;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class CsvExpenseRepository implements ExpenseRepository {

  private final DataLoader dataLoader;

  public CsvExpenseRepository(DataLoader dataLoader) {
    this.dataLoader = dataLoader;
  }

  @Override
  public List<Expense> find(ExpenseQuery query) {
    return filterByCategory(query.category())
        .andThen(filterByDateStart(query.dateStart()))
        .andThen(filterByDateEnd(query.dateEnd()))
        .apply(dataLoader.load());
  }

  private Function<List<Expense>, List<Expense>> filterByCategory(ExpenseType category) {
    if (category == ExpenseType.ALL) {
      return expenses -> expenses;
    }
    return (expenses) -> expenses.stream()
        .filter(expense -> expense.type() == category)
        .collect(Collectors.toList());
  }

  private Function<List<Expense>, List<Expense>> filterByDateStart(LocalDate date) {
    return (expenses) -> expenses.stream()
        .filter(expense -> expense.date().isAfter(date))
        .collect(Collectors.toList());
  }

  private Function<List<Expense>, List<Expense>> filterByDateEnd(LocalDate date) {
    return (expenses) -> expenses.stream()
        .filter(expense -> expense.date().isBefore(date))
        .collect(Collectors.toList());
  }
}
