package com.piggybank.model;


import com.piggybank.expenses.dto.ExpenseType;
import com.piggybank.expenses.repository.ExpenseQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;
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

    @MockBean
    private PrincipalProvider principalProvider;

    @MockBean
    private Principal loggedUser;

    private ExpensesRepository sut;

    private static ExpenseQuery.Builder getBuilder() {
        return ExpenseQuery
                .builder();
    }

    @Before
    public void setUp() {
        sut = new ExpensesRepository(repository, principalProvider);

        Mockito.when(principalProvider.getLoggedUser()).thenReturn("test_user");

        User owner = fakeUser("test_user");
        testEntityManager.persist(owner);

        Expense januaryExpense = fakeExpense(ExpenseType.CASA, "description1", LocalDate.of(2018, Month.JANUARY, 1), owner);
        Expense februaryExpense = fakeExpense(ExpenseType.MOTO, "description2", LocalDate.of(2018, Month.FEBRUARY, 1), owner);
        Expense marchExpense = fakeExpense(ExpenseType.BOLLETTE, "description3", LocalDate.of(2018, Month.MARCH, 1), owner);

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
    public void shouldCallFilterByDateStartAndDateEnd() {
        final LocalDate dateStart = LocalDate.of(2018, Month.JANUARY, 1);
        final LocalDate dateEnd = dateStart.plusMonths(1);

        List<com.piggybank.expenses.dto.Expense> result = sut.find(getBuilder()
                .setDateStart(dateStart)
                .setDateEnd(dateEnd)
                .build());

        Assert.assertEquals(Arrays.asList(
                fakeDtoExpense("description1", ExpenseType.CASA, LocalDate.of(2018, Month.JANUARY, 1)),
                fakeDtoExpense("description2", ExpenseType.MOTO, LocalDate.of(2018, Month.FEBRUARY, 1))),
                result);
    }

    @Test
    public void shouldCallFilterByDateStartOnly() {

        List<com.piggybank.expenses.dto.Expense> result = sut.find(getBuilder()
                .setDateStart(LocalDate.of(2018, Month.FEBRUARY, 1))
                .build());

        Assert.assertEquals(Arrays.asList(
                fakeDtoExpense("description2", ExpenseType.MOTO, LocalDate.of(2018, Month.FEBRUARY, 1)),
                fakeDtoExpense("description3", ExpenseType.BOLLETTE, LocalDate.of(2018, Month.MARCH, 1))),
                result);
    }

    @Test
    public void shouldCallFilterByDateEndOnly() {

        List<com.piggybank.expenses.dto.Expense> result = sut.find(getBuilder()
                .setDateEnd(LocalDate.of(2018, Month.FEBRUARY, 1))
                .build());

        Assert.assertEquals(Arrays.asList(
                fakeDtoExpense("description1", ExpenseType.CASA, LocalDate.of(2018, Month.JANUARY, 1)),
                fakeDtoExpense("description2", ExpenseType.MOTO, LocalDate.of(2018, Month.FEBRUARY, 1))),
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

    @Test
    public void shouldReturnOnlyExpensesAssociatedWithLoggedUser() {
        User otherOwner = fakeUser("other_owner");
        testEntityManager.persist(otherOwner);

        Expense otherExpense = fakeExpense(ExpenseType.BOLLETTE, "test_description", LocalDate.now(), otherOwner);
        testEntityManager.persist(otherExpense);
        testEntityManager.flush();

        List<com.piggybank.expenses.dto.Expense> result = sut.find(getBuilder().build());
        Assert.assertEquals(Arrays.asList(
                fakeDtoExpense("description1", ExpenseType.CASA, LocalDate.of(2018, Month.JANUARY, 1)),
                fakeDtoExpense("description2", ExpenseType.MOTO, LocalDate.of(2018, Month.FEBRUARY, 1)),
                fakeDtoExpense("description3", ExpenseType.BOLLETTE, LocalDate.of(2018, Month.MARCH, 1))),
                result);
    }

    private Expense fakeExpense(ExpenseType casa, String description, LocalDate date, User owner) {
        Expense expense = new Expense();
        expense.setType(casa);
        expense.setDescription(description);
        expense.setDate(date);
        expense.setAmount(100);
        expense.setOwner(owner);
        return expense;
    }

    private User fakeUser(String username) {
        final User user = new User();
        user.setPassword("password");
        user.setToken("token");
        user.setUsername(username);
        return user;
    }

    private com.piggybank.expenses.dto.Expense fakeDtoExpense(String description, ExpenseType expenseType, LocalDate date) {
        return com.piggybank.expenses.dto.Expense
                .newBuilder()
                .setAmount(100L)
                .setDescription(description)
                .setType(expenseType)
                .setDate(date)
                .build();
    }

}