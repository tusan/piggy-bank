package com.piggybank.model;

import com.google.common.base.MoreObjects;
import com.piggybank.expenses.dto.ExpenseType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private ExpenseType type;
    private String description;
    private LocalDate date;
    private double amount;

    @ManyToOne
    private User owner;

    Expense() {
    }

    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    ExpenseType getType() {
        return type;
    }

    void setType(ExpenseType type) {
        this.type = type;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    LocalDate getDate() {
        return date;
    }

    void setDate(LocalDate date) {
        this.date = date;
    }

    double getAmount() {
        return amount;
    }

    void setAmount(double amount) {
        this.amount = amount;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("type", type)
                .add("description", description)
                .add("date", date)
                .add("amount", amount)
                .add("owner", owner)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Double.compare(expense.amount, amount) == 0 &&
                Objects.equals(id, expense.id) &&
                type == expense.type &&
                Objects.equals(description, expense.description) &&
                Objects.equals(date, expense.date) &&
                Objects.equals(owner, expense.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, description, date, amount, owner);
    }

}
