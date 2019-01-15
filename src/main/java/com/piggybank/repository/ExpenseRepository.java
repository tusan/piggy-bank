package com.piggybank.repository;

import com.piggybank.dto.Expense;

import java.util.List;

public interface ExpenseRepository {
    List<Expense> find(ExpenseQuery query);

    void save(Expense expense);
}
