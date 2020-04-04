package com.piggybank.service.expenses;

import com.piggybank.service.expenses.repository.Expense;
import com.piggybank.service.expenses.repository.JpaExpensesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
final class ExpensesServiceImpl implements ExpensesService {
  private final JpaExpensesRepository jpaRepository;

  public ExpensesServiceImpl(final JpaExpensesRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<Expense> find(final Query query) {
    if (query.dateStart() != null && query.dateEnd() != null) {
      return jpaRepository.findByDateBetweenAndOwner(
          query.dateStart(), query.dateEnd(), query.owner());
    } else if (query.dateStart() != null) {
      return jpaRepository.findByDateGreaterThanEqualAndOwner(query.dateStart(), query.owner());
    } else if (query.dateEnd() != null) {
      return jpaRepository.findByDateLessThanEqualAndOwner(query.dateEnd(), query.owner());
    } else {
      return jpaRepository.findByOwner(query.owner());
    }
  }

  @Override
  public Optional<Long> save(final Expense expense) {
    final Expense result = jpaRepository.save(expense);
    return Optional.ofNullable(result.getId());
  }
}
