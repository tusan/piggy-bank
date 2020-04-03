package com.piggybank.service.expenses;

import com.google.auto.value.AutoValue;
import com.piggybank.service.expenses.dto.ExpenseDto;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IExpensesService {
  List<ExpenseDto> find(Query query);
  Optional<Long> save(ExpenseDto expenseDto);

  @AutoValue
  abstract class Query {

    public static Builder builder() {
      return new AutoValue_IExpensesService_Query.Builder();
    }

    @Nullable
    public abstract LocalDate dateStart();

    @Nullable
    public abstract LocalDate dateEnd();

    @AutoValue.Builder
    public abstract static class Builder {
      public abstract Builder setDateStart(LocalDate dateStart);

      public abstract Builder setDateEnd(LocalDate dateEnd);

      public abstract Query build();
    }
  }
}
