package com.piggybank.model;


import com.piggybank.expenses.dto.ExpenseType;
import com.piggybank.expenses.repository.ExpenseQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ExpensesRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaExpensesRepository repository;

    private ExpensesRepository sut;

    @Before
    public void setUp() {
        sut = new ExpensesRepository(repository);

        Expense januaryExpense = new Expense();
        januaryExpense.setType(ExpenseType.CASA);
        januaryExpense.setDescription("description1");
        januaryExpense.setDate(LocalDate.of(2018, Month.JANUARY, 1));
        januaryExpense.setAmount(100);

        Expense februaryExpense = new Expense();
        februaryExpense.setType(ExpenseType.MOTO);
        februaryExpense.setDescription("description2");
        februaryExpense.setDate(LocalDate.of(2018, Month.FEBRUARY, 1));
        februaryExpense.setAmount(100);

        Expense marchExpense = new Expense();
        marchExpense.setType(ExpenseType.BOLLETTE);
        marchExpense.setDescription("description3");
        marchExpense.setDate(LocalDate.of(2018, Month.MARCH, 1));
        marchExpense.setAmount(100);

        testEntityManager.persist(januaryExpense);
        testEntityManager.persist(februaryExpense);
        testEntityManager.persist(marchExpense);

        testEntityManager.flush();
    }

    @Test
    public void shouldCallFilterByDateStartAndDateEnd() {
        final LocalDate dateStart = LocalDate.of(2018, Month.JANUARY, 1);
        final LocalDate dateEnd = dateStart.plusMonths(1);

        List<com.piggybank.expenses.dto.Expense> result = sut.find(ExpenseQuery
                .builder()
                .setDateStart(dateStart)
                .setDateEnd(dateEnd)
                .build());

        Assert.assertEquals(Arrays.asList(
                com.piggybank.expenses.dto.Expense
                        .newBuilder()
                        .setAmount(100L)
                        .setDescription("description1")
                        .setType(ExpenseType.CASA)
                        .setDate(LocalDate.of(2018, Month.JANUARY, 1))
                        .build(),
                com.piggybank.expenses.dto.Expense
                        .newBuilder()
                        .setAmount(100L)
                        .setDescription("description2")
                        .setType(ExpenseType.MOTO)
                        .setDate(LocalDate.of(2018, Month.FEBRUARY, 1))
                        .build()),
                result);
    }

    @Test
    public void shouldCallFilterByDateStartOnly() {

        List<com.piggybank.expenses.dto.Expense> result = sut.find(ExpenseQuery
                .builder()
                .setDateStart(LocalDate.of(2018, Month.FEBRUARY, 1))
                .build());

        Assert.assertEquals(Arrays.asList(com.piggybank.expenses.dto.Expense
                        .newBuilder()
                        .setAmount(100L)
                        .setDescription("description2")
                        .setType(ExpenseType.MOTO)
                        .setDate(LocalDate.of(2018, Month.FEBRUARY, 1))
                        .build(),
                com.piggybank.expenses.dto.Expense
                        .newBuilder()
                        .setAmount(100L)
                        .setDescription("description3")
                        .setType(ExpenseType.BOLLETTE)
                        .setDate(LocalDate.of(2018, Month.MARCH, 1))
                        .build()),
                result);
    }

    @Test
    public void shouldCallFilterByDateEndOnly() {

        List<com.piggybank.expenses.dto.Expense> result = sut.find(ExpenseQuery
                .builder()
                .setDateEnd(LocalDate.of(2018, Month.FEBRUARY, 1))
                .build());

        Assert.assertEquals(Arrays.asList(
                com.piggybank.expenses.dto.Expense
                        .newBuilder()
                        .setAmount(100L)
                        .setDescription("description1")
                        .setType(ExpenseType.CASA)
                        .setDate(LocalDate.of(2018, Month.JANUARY, 1))
                        .build(),
                com.piggybank.expenses.dto.Expense
                        .newBuilder()
                        .setAmount(100L)
                        .setDescription("description2")
                        .setType(ExpenseType.MOTO)
                        .setDate(LocalDate.of(2018, Month.FEBRUARY, 1))
                        .build()),
                result);
    }

    @Test
    public void shouldSaveTheExpense() {
        final LocalDate date = LocalDate.now();

        Expense result = sut.save(com.piggybank.expenses.dto.Expense
                .newBuilder()
                .setAmount(123L)
                .setDescription("description")
                .setType(ExpenseType.CASA)
                .setDate(date)
                .build());

        Expense expected = new Expense();
        expected.setId(result.getId());
        expected.setAmount(123L);
        expected.setDescription("description");
        expected.setType(ExpenseType.CASA);
        expected.setDate(date);


        Assert.assertEquals(expected, repository.findById(result.getId()).get());
    }
}