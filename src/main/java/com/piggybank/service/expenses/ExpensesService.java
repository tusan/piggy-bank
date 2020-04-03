package com.piggybank.service.expenses;

import com.piggybank.service.expenses.dto.ExpenseDto;
import com.piggybank.service.expenses.repository.Expense;
import com.piggybank.service.expenses.repository.JpaExpensesRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
class ExpensesService implements IExpensesService {
  private final JpaExpensesRepository jpaRepository;

  ExpensesService(final JpaExpensesRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<ExpenseDto> find(final Query query) {
    return jpaRepository.findAll(getQueryFilters(query)).stream()
        .map(this::convertToDtoExpense)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Long> save(final ExpenseDto expenseDto) {
    Expense result = jpaRepository.save(convertToEntityExpense(expenseDto));
    return Optional.ofNullable(result.getId());
  }

  private Specification<Expense> getQueryFilters(final Query query) {
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
        .map(dateStart -> (root, q, criteriaBuilder) ->
            criteriaBuilder.greaterThanOrEqualTo(root.get("date"), dateStart));
  }

  private Optional<Specification<Expense>> dateEndFilter(final Query query) {
    return Optional.ofNullable(query.dateEnd())
        .map(dateEnd -> (root, q, criteriaBuilder) ->
            criteriaBuilder.lessThanOrEqualTo(root.get("date"), dateEnd));
  }

  private Expense convertToEntityExpense(final ExpenseDto expenseDto) {
    final Expense exp = new Expense();

    exp.setType(expenseDto.type());
    exp.setDate(expenseDto.date());
    exp.setAmount(expenseDto.amount());
    exp.setDescription(expenseDto.description());

    return exp;
  }

  private ExpenseDto convertToDtoExpense(final Expense exp) {
    return ExpenseDto.newBuilder()
        .setAmount(exp.getAmount())
        .setDate(exp.getDate())
        .setDescription(exp.getDescription())
        .setType(exp.getType())
        .build();
  }

}
