package com.piggybank.repository;

import com.piggybank.model.Expense;

import java.util.List;

public interface ExpenseRepository {
    List<Expense> find(ExpenseQuery query);
}
