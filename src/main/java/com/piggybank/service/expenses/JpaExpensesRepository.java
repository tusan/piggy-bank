package com.piggybank.service.expenses;

import com.piggybank.service.users.PiggyBankUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

interface JpaExpensesRepository
    extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
  List<Expense> findByOwner(PiggyBankUser owner);

  List<Expense> findByDateLessThanEqualAndOwner(LocalDate date, PiggyBankUser owner);

  List<Expense> findByDateGreaterThanEqualAndOwner(LocalDate date, PiggyBankUser owner);

  List<Expense> findByDateBetweenAndOwner(
      LocalDate dateStart, LocalDate dateEnd, PiggyBankUser owner);
}
