package com.piggybank.model;

import com.piggybank.expenses.dto.ExpenseType;
import org.hamcrest.Matchers;
import org.junit.After;
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
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JpaExpensesRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaExpensesRepository sut;

    @Before
    public void setUp() {
        Expense januaryExpense = new Expense();
        januaryExpense.setType(ExpenseType.CASA);
        januaryExpense.setDescription("description1");
        januaryExpense.setDate(LocalDate.of(218, Month.JANUARY, 1));
        januaryExpense.setAmount(100);

        Expense februaryExpense = new Expense();
        februaryExpense.setType(ExpenseType.MOTO);
        februaryExpense.setDescription("description2");
        februaryExpense.setDate(LocalDate.of(218, Month.FEBRUARY, 1));
        februaryExpense.setAmount(100);

        Expense marchExpense = new Expense();
        marchExpense.setType(ExpenseType.BOLLETTE);
        marchExpense.setDescription("description3");
        marchExpense.setDate(LocalDate.of(218, Month.MARCH, 1));
        marchExpense.setAmount(100);

        testEntityManager.persist(januaryExpense);
        testEntityManager.persist(februaryExpense);
        testEntityManager.persist(marchExpense);

        testEntityManager.flush();
    }

    @After
    public void tearDown() {
        testEntityManager.clear();
        testEntityManager.flush();
    }

    @Test
    public void shouldFilterByDateStartAndDateEnd() {
        List<Expense> expenses = sut.findByDateGreaterThanEqualAndDateLessThanEqual(
                LocalDate.of(218, Month.FEBRUARY, 1),
                LocalDate.of(218, Month.FEBRUARY, 28)
        );

        Expense expected = new Expense();
        expected.setType(ExpenseType.MOTO);
        expected.setDescription("description2");
        expected.setDate(LocalDate.of(218, Month.FEBRUARY, 1));
        expected.setAmount(100);

        Assert.assertThat(expenses,
                Matchers.equalTo(Collections.singletonList(
                        expected)));
    }

    @Test
    public void shouldFilterByDateStartOnly() {
        List<Expense> expenses = sut.findByDateGreaterThanEqual(
                LocalDate.of(218, Month.FEBRUARY, 1));

        Expense expected1 = new Expense();
        expected1.setType(ExpenseType.MOTO);
        expected1.setDescription("description2");
        expected1.setDate(LocalDate.of(218, Month.FEBRUARY, 1));
        expected1.setAmount(100);

        Expense expected2 = new Expense();
        expected2.setType(ExpenseType.BOLLETTE);
        expected2.setDescription("description3");
        expected2.setDate(LocalDate.of(218, Month.MARCH, 1));
        expected2.setAmount(100);

        Assert.assertThat(expenses,
                Matchers.equalTo(Arrays.asList(
                        expected1,
                        expected2)));
    }

    @Test
    public void shouldFilterByDateEndOnly() {
        List<Expense> expenses = sut.findByDateLessThanEqual(
                LocalDate.of(218, Month.FEBRUARY, 28));

        Expense expected1 = new Expense();
        expected1.setType(ExpenseType.CASA);
        expected1.setDescription("description1");
        expected1.setDate(LocalDate.of(218, Month.JANUARY, 1));
        expected1.setAmount(100);

        Expense expected2 = new Expense();
        expected2.setType(ExpenseType.MOTO);
        expected2.setDescription("description2");
        expected2.setDate(LocalDate.of(218, Month.FEBRUARY, 1));
        expected2.setAmount(100);

        Assert.assertThat(expenses,
                Matchers.equalTo(Arrays.asList(
                        expected1,
                        expected2)));
    }
}