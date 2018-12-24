package com.piggybank.repository.dataloader;

import com.piggybank.model.Expense;
import java.util.List;

public interface DataLoader {
  List<Expense> load();
}
