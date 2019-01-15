package com.piggybank.repository.postgres.jpa;

import com.piggybank.repository.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JpaExpensesRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByDateGreaterThanEqualAndDateLessThanEqual(LocalDate dateStart, LocalDate dateEnd);

    List<Expense> findByDateGreaterThanEqual(LocalDate dateStart);

    List<Expense> findByDateLessThanEqual(LocalDate dateEnd);

}