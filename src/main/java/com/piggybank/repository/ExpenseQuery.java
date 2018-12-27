package com.piggybank.repository;

import com.google.auto.value.AutoValue;
import java.time.LocalDate;

@AutoValue
public abstract class ExpenseQuery {

    public abstract LocalDate dateStart();

    public abstract LocalDate dateEnd();

    public static Builder builder() {
        return new AutoValue_ExpenseQuery.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setDateStart(LocalDate dateStart);

        public abstract Builder setDateEnd(LocalDate dateEnd);

        public abstract ExpenseQuery build();
    }
}
