package com.piggybank.service.expenses;

import com.google.auto.value.AutoValue;
import com.piggybank.service.users.PiggyBankUser;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpensesService {
  List<Expense> find(Query query);

  Optional<Long> save(Expense expenseDto);

  @AutoValue
  abstract class Query {

    public static Builder builder(final PiggyBankUser owner) {
      return new AutoValue_ExpensesService_Query.Builder().setOwner(owner);
    }

    @Nullable
    public abstract LocalDate dateStart();

    @Nullable
    public abstract LocalDate dateEnd();

    public abstract PiggyBankUser owner();

    @AutoValue.Builder
    public abstract static class Builder {
      public abstract Builder setDateStart(LocalDate dateStart);

      public abstract Builder setDateEnd(LocalDate dateEnd);

      public abstract Builder setOwner(PiggyBankUser owner);

      public abstract Query build();
    }
  }
}
