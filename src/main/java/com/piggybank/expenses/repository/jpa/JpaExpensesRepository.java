package com.piggybank.expenses.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
interface JpaExpensesRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByDateGreaterThanEqualAndDateLessThanEqual(LocalDate dateStart, LocalDate dateEnd);

    List<Expense> findByDateGreaterThanEqual(LocalDate dateStart);

    List<Expense> findByDateLessThanEqual(LocalDate dateEnd);

}