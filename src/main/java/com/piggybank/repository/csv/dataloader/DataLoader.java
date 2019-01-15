package com.piggybank.repository.csv.dataloader;

import com.piggybank.dto.Expense;

import java.util.List;

public interface DataLoader {
    List<Expense> load();

    void save(Expense expense);
}
