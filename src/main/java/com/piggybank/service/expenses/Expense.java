package com.piggybank.service.expenses;

import com.google.common.base.MoreObjects;
import com.piggybank.api.expenses.ExpenseType;
import com.piggybank.service.users.PiggyBankUser;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public final class Expense {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private ExpenseType type;
  private String description;
  private LocalDate date;
  private double amount;

  @ManyToOne private PiggyBankUser owner;

  public Expense() {}

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public ExpenseType getType() {
    return type;
  }

  public void setType(final ExpenseType type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(final LocalDate date) {
    this.date = date;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(final double amount) {
    this.amount = amount;
  }

  public void setOwner(final PiggyBankUser owner) {
    this.owner = owner;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("type", type)
        .add("description", description)
        .add("date", date)
        .add("amount", amount)
        .add("owner", owner)
        .toString();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final Expense expense = (Expense) o;
    return Double.compare(expense.amount, amount) == 0
        && type == expense.type
        && Objects.equals(description, expense.description)
        && Objects.equals(date, expense.date)
        && Objects.equals(owner, expense.owner);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, description, date, amount, owner);
  }
}
