package com.piggybank.expenses.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.time.LocalDate;

@AutoValue
@JsonDeserialize(builder = Expense.Builder.class)
public abstract class Expense {

    public static Builder newBuilder() {
        return new AutoValue_Expense.Builder();
    }

    @JsonProperty("type")
    public abstract ExpenseType type();

    @Nullable
    @JsonProperty("description")
    public abstract String description();

    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    public abstract LocalDate date();

    @JsonProperty("amount")
    public abstract double amount();

    @AutoValue.Builder
    @JsonPOJOBuilder
    public abstract static class Builder {

        @JsonCreator
        private static Builder create() {
            return newBuilder();
        }

        @JsonProperty("type")
        public abstract Builder setType(ExpenseType type);

        @JsonProperty("description")
        @Nullable
        public abstract Builder setDescription(String description);

        @JsonProperty("date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
        public abstract Builder setDate(LocalDate date);

        @JsonProperty("amount")
        public abstract Builder setAmount(double amount);

        public abstract Expense build();
    }
}