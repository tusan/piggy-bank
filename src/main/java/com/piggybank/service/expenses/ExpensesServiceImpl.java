package com.piggybank.service.expenses;

import com.piggybank.service.expenses.repository.Expense;
import com.piggybank.service.expenses.repository.JpaExpensesRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
final class ExpensesServiceImpl implements ExpensesService {
  private final JpaExpensesRepository jpaRepository;

  public ExpensesServiceImpl(final JpaExpensesRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<Expense> find(final Query query) {
    return jpaRepository.findAll(applyQueryFilters(query));
  }

  @Override
  public Optional<Long> save(final Expense expense) {
    final Expense result = jpaRepository.save(expense);
    return Optional.ofNullable(result.getId());
  }

  //TODO replace with more testable implementation
  private Specification<Expense> applyQueryFilters(final Query query) {
    return Stream.of(dateStartFilter(query), dateEndFilter(query))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .reduce(withOwner(query.owner()), Specification::and);
  }

  private Specification<Expense> withOwner(final String owner) {
    return (root, q, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("owner").get("username"), owner);
  }

  private Optional<Specification<Expense>> dateStartFilter(final Query query) {
    return Optional.ofNullable(query.dateStart())
        .map(
            dateStart ->
                (root, q, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("date"), dateStart));
  }

  private Optional<Specification<Expense>> dateEndFilter(final Query query) {
    return Optional.ofNullable(query.dateEnd())
        .map(
            dateEnd ->
                (root, q, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("date"), dateEnd));
  }
}
