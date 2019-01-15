package com.piggybank.repository.csv;

import com.piggybank.dto.Expense;
import com.piggybank.repository.ExpenseQuery;
import com.piggybank.repository.ExpenseRepository;
import com.piggybank.repository.csv.dataloader.DataLoader;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class CsvExpenseRepository implements ExpenseRepository {

    private final DataLoader dataLoader;

    public CsvExpenseRepository(final DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Override
    public List<Expense> find(final ExpenseQuery query) {
        return filterByDateStart(query.dateStart())
                .andThen(filterByDateEnd(query.dateEnd()))
                .apply(dataLoader.load());
    }

    @Override
    public void save(final Expense expense) {
        dataLoader.save(expense);
    }

    private Function<List<Expense>, List<Expense>> filterByDateStart(final LocalDate date) {
        return (expenses) -> expenses.stream()
                .filter(expense -> expense.date().isAfter(date))
                .collect(Collectors.toList());
    }

    private Function<List<Expense>, List<Expense>> filterByDateEnd(final LocalDate date) {
        return (expenses) -> expenses.stream()
                .filter(expense -> expense.date().isEqual(date) || expense.date().isBefore(date))
                .collect(Collectors.toList());
    }
}
