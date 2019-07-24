package com.piggybank.model;

import com.piggybank.expenses.dto.Expense;
import com.piggybank.expenses.repository.ExpenseQuery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class ExpensesRepository {
  private final JpaExpensesRepository jpaRepository;
  private final PrincipalProvider principalProvider;

  ExpensesRepository(JpaExpensesRepository jpaRepository, PrincipalProvider principalProvider) {
    this.jpaRepository = jpaRepository;
    this.principalProvider = principalProvider;
  }

  public List<Expense> find(final ExpenseQuery query) {
    return jpaRepository.findAll(getQueryFilters(query)).stream()
        .map(this::convertToDtoExpense)
        .collect(Collectors.toList());
  }

  public com.piggybank.model.Expense save(final Expense expense) {
    return jpaRepository.save(convertToEntityExpense(expense));
  }

  private Specification<com.piggybank.model.Expense> owner() {
    return (root, q, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("owner").get("username"), principalProvider.getLoggedUser());
  }

  private Specification<com.piggybank.model.Expense> getQueryFilters(ExpenseQuery query) {
    return Stream.of(dateStartFilter(query), dateEndFilter(query))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .reduce(owner(), Specification::and);
  }

  private Optional<Specification<com.piggybank.model.Expense>> dateStartFilter(
      final ExpenseQuery query) {
    return Optional.ofNullable(query.dateStart())
        .map(
            dateStart ->
                (root, q, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("date"), dateStart));
  }

  private Optional<Specification<com.piggybank.model.Expense>> dateEndFilter(
      final ExpenseQuery query) {
    return Optional.ofNullable(query.dateEnd())
        .map(
            dateEnd ->
                (root, q, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("date"), dateEnd));
  }

  private com.piggybank.model.Expense convertToEntityExpense(final Expense expense) {
    com.piggybank.model.Expense exp = new com.piggybank.model.Expense();

    exp.setType(expense.type());
    exp.setDate(expense.date());
    exp.setAmount(expense.amount());
    exp.setDescription(expense.description());
    return exp;
  }

  private Expense convertToDtoExpense(final com.piggybank.model.Expense exp) {
    return Expense.newBuilder()
        .setAmount(exp.getAmount())
        .setDate(exp.getDate())
        .setDescription(exp.getDescription())
        .setType(exp.getType())
        .build();
  }
}
