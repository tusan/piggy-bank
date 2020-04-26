package com.piggybank.api.expenses;

import com.piggybank.api.expenses.dto.ExpenseDto;
import com.piggybank.service.authentication.repository.PiggyBankUser;
import com.piggybank.service.expenses.repository.Expense;

final class ExpenseConverter {
  static Expense toEntity(final ExpenseDto expenseDto, final PiggyBankUser owner) {
    final Expense exp = new Expense();

    exp.setType(expenseDto.type());
    exp.setDate(expenseDto.date());
    exp.setAmount(expenseDto.amount());
    exp.setDescription(expenseDto.description());
    exp.setOwner(owner);

    return exp;
  }

  static ExpenseDto toDto(final Expense exp) {
    return ExpenseDto.newBuilder()
        .setAmount(exp.getAmount())
        .setDate(exp.getDate())
        .setDescription(exp.getDescription())
        .setType(exp.getType())
        .build();
  }
}