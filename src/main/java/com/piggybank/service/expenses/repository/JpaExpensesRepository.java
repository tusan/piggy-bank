package com.piggybank.service.expenses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaExpensesRepository
    extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {}
