package com.piggybank.expenses.repository;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.time.LocalDate;

@AutoValue
public abstract class ExpenseQuery {

    public static Builder builder() {
        return new AutoValue_ExpenseQuery.Builder();
    }

    @Nullable
    public abstract LocalDate dateStart();

    @Nullable
    public abstract LocalDate dateEnd();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setDateStart(LocalDate dateStart);

        public abstract Builder setDateEnd(LocalDate dateEnd);

        public abstract ExpenseQuery build();
    }
}
