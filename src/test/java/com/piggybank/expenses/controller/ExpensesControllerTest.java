package com.piggybank.expenses.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piggybank.expenses.dto.Expense;
import com.piggybank.expenses.dto.ExpenseType;
import com.piggybank.expenses.repository.ExpenseQuery;
import com.piggybank.model.ExpensesRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ExpensesControllerTest {

    @InjectMocks
    private ExpensesController sut;

    @Mock
    private ExpensesRepository expenseRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(
                        new ObjectMapper().registerModule(new JavaTimeModule())))
                .build();
    }


    @Test
    public void shouldReturn200AndTheTransferObjectsInDateRange() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses")
                .param("date-start", "20181107")
                .param("date-end", "20181207"))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(expenseRepository).find(ExpenseQuery.builder()
                .setDateStart(LocalDate.of(2018, 11, 7))
                .setDateEnd(LocalDate.of(2018, 12, 7))
                .build());
    }

    @Test
    public void shouldReturn201AndCallSaveOnRepository() throws Exception {
        final String json = "{\n"
                + "  \"date\": \"20181107\",\n"
                + "  \"type\": \"CASA\",\n"
                + "  \"amount\": \"22.57\",\n"
                + "  \"description\": \"test description\"\n"
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        Mockito.verify(expenseRepository).save(Expense.newBuilder()
                .setDate(LocalDate.of(2018, 11, 7))
                .setType(ExpenseType.CASA)
                .setAmount(22.57)
                .setDescription("test description")
                .build());
    }

    @Test
    public void shouldReturnInternalServerErrorWhenRepositoryFails() throws Exception {
        final String json = "{\n"
                + "  \"date\": \"20181107\",\n"
                + "  \"type\": \"CASA\",\n"
                + "  \"amount\": \"22.57\",\n"
                + "  \"description\": \"test description\"\n"
                + "}";

        Mockito.doThrow(new RuntimeException("test exception"))
                .when(expenseRepository).save(ArgumentMatchers.any(Expense.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isInternalServerError())
                .andReturn();

        Mockito.verify(expenseRepository).save(Expense.newBuilder()
                .setDate(LocalDate.of(2018, 11, 7))
                .setType(ExpenseType.CASA)
                .setAmount(22.57)
                .setDescription("test description")
                .build());
    }
}