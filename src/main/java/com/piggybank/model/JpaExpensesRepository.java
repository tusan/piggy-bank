package com.piggybank.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
interface JpaExpensesRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
}