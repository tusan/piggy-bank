package com.piggybank.expenses.repository.jpa;


import com.piggybank.expenses.dto.ExpenseType;
import com.piggybank.expenses.repository.ExpenseQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ExpensesRepositoryTest {
    @InjectMocks
    private ExpensesRepository sut;

    @Mock
    private JpaExpensesRepository repository;

    private static com.piggybank.expenses.dto.Expense fakeExpenseDto(LocalDate date) {
        return com.piggybank.expenses.dto.Expense
                .newBuilder()
                .setAmount(123L)
                .setDescription("description")
                .setType(ExpenseType.CASA)
                .setDate(date)
                .build();
    }

    private static Expense fakeExpense(LocalDate dateStart) {
        final Expense fakeExpense = new Expense();
        fakeExpense.setId(0L);
        fakeExpense.setAmount(123L);
        fakeExpense.setDescription("description");
        fakeExpense.setType(ExpenseType.CASA);
        fakeExpense.setDate(dateStart);
        return fakeExpense;
    }

    @Test
    public void shouldCallFilterByDateStartAndDateEnd() {
        final LocalDate dateStart = LocalDate.now();
        final LocalDate dateEnd = dateStart.plusDays(10);

        Mockito.when(repository.findByDateGreaterThanEqualAndDateLessThanEqual(Mockito.any(LocalDate.class), Mockito.any(LocalDate.class)))
                .thenReturn(Collections.singletonList(fakeExpense(dateStart)));

        List<com.piggybank.expenses.dto.Expense> result = sut.find(ExpenseQuery
                .builder()
                .setDateStart(dateStart)
                .setDateEnd(dateEnd)
                .build());

        Assert.assertEquals(Collections.singletonList(fakeExpenseDto(dateStart)),
                result);

        Mockito.verify(repository).findByDateGreaterThanEqualAndDateLessThanEqual(dateStart, dateEnd);
    }

    @Test
    public void shouldCallFilterByDateStartOnly() {
        final LocalDate dateStart = LocalDate.now();

        Mockito.when(repository.findByDateGreaterThanEqual(Mockito.any(LocalDate.class)))
                .thenReturn(Collections.singletonList(fakeExpense(dateStart)));

        List<com.piggybank.expenses.dto.Expense> result = sut.find(ExpenseQuery
                .builder()
                .setDateStart(dateStart)
                .build());

        Assert.assertEquals(Collections.singletonList(fakeExpenseDto(dateStart)),
                result);

        Mockito.verify(repository).findByDateGreaterThanEqual(dateStart);
    }

    @Test
    public void shouldCallFilterByDateEndOnly() {
        final LocalDate date = LocalDate.now();

        Mockito.when(repository.findByDateLessThanEqual(Mockito.any(LocalDate.class)))
                .thenReturn(Collections.singletonList(fakeExpense(date)));

        List<com.piggybank.expenses.dto.Expense> result = sut.find(ExpenseQuery
                .builder()
                .setDateEnd(date)
                .build());

        Assert.assertEquals(Collections.singletonList(fakeExpenseDto(date)),
                result);

        Mockito.verify(repository).findByDateLessThanEqual(date);
    }

    @Test
    public void shouldSaveTheExpense() {
        final LocalDate date = LocalDate.now();
        sut.save(fakeExpenseDto(date));

        Mockito.verify(repository).save(fakeExpense(date));
    }
}