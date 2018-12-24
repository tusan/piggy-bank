package com.piggybank.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class PiggyBankControllerTest {
    @Mock
    private PiggyBankController sut;

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
    }
}