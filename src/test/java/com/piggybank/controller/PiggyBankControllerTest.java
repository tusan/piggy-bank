package com.piggybank.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.piggybank.model.ExpenseType;
import com.piggybank.repository.ExpenseQuery;
import com.piggybank.repository.ExpenseRepository;
import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
public class PiggyBankControllerTest {

    @InjectMocks
    private PiggyBankController sut;

    @Mock
    private ExpenseRepository expenseRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
    }

    @Test
    public void shouldReturn200AndTheTransferObjectsInDateRange() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/expenses")
                .param("date-start", "20181107")
                .param("date-end", "20181207")
                .param("category", "MOTO"))
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(expenseRepository).find(ExpenseQuery.builder()
            .setCategory(ExpenseType.MOTO)
            .setDateStart(LocalDate.of(2018, 11, 07))
            .setDateEnd(LocalDate.of(2018, 12, 07))
            .build());
    }
}