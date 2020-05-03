package com.piggybank.service.expenses;

import com.piggybank.service.users.PiggyBankUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

interface JpaExpensesRepository
    extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
  List<Expense> findByOwner(final PiggyBankUser owner);

  List<Expense> findByDateLessThanEqualAndOwner(final LocalDate date, final PiggyBankUser owner);

  List<Expense> findByDateGreaterThanEqualAndOwner(final LocalDate date, final PiggyBankUser owner);

  List<Expense> findByDateBetweenAndOwner(
      final LocalDate dateStart, final LocalDate dateEnd, final PiggyBankUser owner);
}
