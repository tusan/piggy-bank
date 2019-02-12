package com.piggybank.expenses.repository.jpa;

import com.piggybank.expenses.dto.Expense;
import com.piggybank.expenses.repository.ExpenseQuery;
import com.piggybank.expenses.repository.ExpenseRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Primary
@Repository
public class ExpensesRepository implements ExpenseRepository {
    private final JpaExpensesRepository jpaRepository;

    public ExpensesRepository(JpaExpensesRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Expense> find(final ExpenseQuery query) {
        return execQuery(query)
                .stream()
                .map(this::convertToDtoExpense)
                .collect(Collectors.toList());
    }

    @Override
    public void save(final Expense expense) {
        jpaRepository.save(convertToEntityExpense(expense));
    }

    private List<com.piggybank.expenses.repository.jpa.Expense> execQuery(final ExpenseQuery query) {
        if (query.dateStart() != null && query.dateEnd() != null) {
            return jpaRepository.findByDateGreaterThanEqualAndDateLessThanEqual(query.dateStart(), query.dateEnd());
        } else if (query.dateStart() != null) {
            return jpaRepository.findByDateGreaterThanEqual(query.dateStart());
        } else if (query.dateEnd() != null) {
            return jpaRepository.findByDateLessThanEqual(query.dateEnd());
        } else {
            return jpaRepository.findAll();
        }
    }

    private com.piggybank.expenses.repository.jpa.Expense convertToEntityExpense(final Expense expense) {
        com.piggybank.expenses.repository.jpa.Expense exp =
                new com.piggybank.expenses.repository.jpa.Expense();

        exp.setType(expense.type());
        exp.setDate(expense.date());
        exp.setAmount(expense.amount());
        exp.setDescription(expense.description());
        return exp;
    }

    private Expense convertToDtoExpense(final com.piggybank.expenses.repository.jpa.Expense exp) {
        return Expense.newBuilder()
                .setAmount(exp.getAmount())
                .setDate(exp.getDate())
                .setDescription(exp.getDescription())
                .setType(exp.getType())
                .build();
    }
}
