package com.piggybank.model;

import com.piggybank.expenses.dto.ExpenseType;
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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JpaUserRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaUserRepository sut;

    @Test
    public void shouldReturnCorrectUserWhenPassingUsername() {
        final User user1 = new User();
        user1.setPassword("password1");
        user1.setToken("token1");
        user1.setUsername("username1");

        final User user2 = new User();
        user2.setPassword("password2");
        user2.setToken("token2");
        user2.setUsername("username2");

        testEntityManager.persistAndFlush(user1);
        testEntityManager.persistAndFlush(user2);

        final User expected = new User();
        expected.setPassword("password1");
        expected.setToken("token1");
        expected.setUsername("username1");

        Optional<User> user = sut.findByUsername("username1");
        assertTrue(user.isPresent());
        assertEquals(expected, user.get());
    }

    @Test
    public void shouldReturnAllTheRelatedExpenses() {
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

        List<Expense> expenses = Arrays.asList(januaryExpense, februaryExpense, marchExpense);

        final User expected = new User();
        expected.setPassword("password1");
        expected.setToken("token1");
        expected.setUsername("username1");
        expected.setExpenses(expenses);

        final User user = new User();
        user.setPassword("password1");
        user.setToken("token1");
        user.setUsername("username1");
        user.setExpenses(expenses);

        testEntityManager.persistAndFlush(user);

        Optional<User> actual = sut.findByUsername("username1");
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
        assertTrue(actual.get().getExpenses().size() == 3);
    }
}