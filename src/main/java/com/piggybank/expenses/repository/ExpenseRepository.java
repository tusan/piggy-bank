package com.piggybank.expenses.repository;

import com.piggybank.expenses.dto.Expense;

import java.util.List;

public interface ExpenseRepository {
    List<Expense> find(ExpenseQuery query);

    void save(Expense expense);
}
