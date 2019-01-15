package com.piggybank.repository.postgres;

import com.piggybank.dto.Expense;
import com.piggybank.repository.ExpenseQuery;
import com.piggybank.repository.ExpenseRepository;
import com.piggybank.repository.postgres.jpa.JpaExpensesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Primary
@Repository
public class PostgresExpensesRepository implements ExpenseRepository {
    @Autowired
    private JpaExpensesRepository jpaRepository;

    @Override
    public List<Expense> find(ExpenseQuery query) {
        return execQuery(query)
                .stream()
                .map(exp -> convertToDtoExpense(exp))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Expense expense) {
        jpaRepository.save(convertToEntityExpense(expense));
    }

    private List<com.piggybank.repository.entity.Expense> execQuery(ExpenseQuery query) {
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

    private com.piggybank.repository.entity.Expense convertToEntityExpense(Expense expense) {
        com.piggybank.repository.entity.Expense exp =
                new com.piggybank.repository.entity.Expense();

        exp.setType(expense.type());
        exp.setDate(expense.date());
        exp.setAmount(expense.amount());
        exp.setDescription(expense.description());
        return exp;
    }

    private Expense convertToDtoExpense(com.piggybank.repository.entity.Expense exp) {
        return Expense.newBuilder()
                .setAmount(exp.getAmount())
                .setDate(exp.getDate())
                .setDescription(exp.getDescription())
                .setType(exp.getType())
                .build();
    }
}
