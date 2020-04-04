package com.piggybank.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piggybank.service.expenses.IExpensesService;
import com.piggybank.service.expenses.dto.ExpenseDto;
import com.piggybank.service.expenses.dto.ExpenseType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ExpensesControllerTest {

  @InjectMocks private ExpensesController sut;

  @Mock private IExpensesService expenseRepository;

  @Mock private PrincipalProvider principalProvider;

  private MockMvc mockMvc;

  @Before
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(sut)
            .setMessageConverters(
                new MappingJackson2HttpMessageConverter(
                    new ObjectMapper().registerModule(new JavaTimeModule())))
            .build();

    when(principalProvider.getLoggedUser()).thenReturn("logger_user");
  }

  @Test
  public void shouldReturn200AndTheTransferObjectsInDateRange() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/expenses")
                .param("date-start", "20181107")
                .param("date-end", "20181207"))
        .andExpect(status().isOk())
        .andReturn();

    verify(expenseRepository)
        .find(
            IExpensesService.Query.builder("logger_user")
                .setDateStart(LocalDate.of(2018, 11, 7))
                .setDateEnd(LocalDate.of(2018, 12, 7))
                .build());
  }

  @Test
  public void shouldReturn201AndCallSaveOnRepository() throws Exception {
    final String json =
        "{\n"
            + "  \"date\": \"20181107\",\n"
            + "  \"type\": \"HOUSE\",\n"
            + "  \"amount\": \"22.57\",\n"
            + "  \"description\": \"test description\"\n"
            + "}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isCreated())
        .andReturn();

    verify(expenseRepository)
        .save(
            ExpenseDto.newBuilder()
                .setDate(LocalDate.of(2018, 11, 7))
                .setType(ExpenseType.HOUSE)
                .setAmount(22.57)
                .setDescription("test description")
                .build());
  }

  @Test
  public void shouldReturnInternalServerErrorWhenRepositoryFails() throws Exception {
    final String json =
        "{\n"
            + "  \"date\": \"20181107\",\n"
            + "  \"type\": \"HOUSE\",\n"
            + "  \"amount\": \"22.57\",\n"
            + "  \"description\": \"test description\"\n"
            + "}";

    doThrow(new RuntimeException("test exception"))
        .when(expenseRepository)
        .save(ArgumentMatchers.any(ExpenseDto.class));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isInternalServerError())
        .andReturn();

    verify(expenseRepository)
        .save(
            ExpenseDto.newBuilder()
                .setDate(LocalDate.of(2018, 11, 7))
                .setType(ExpenseType.HOUSE)
                .setAmount(22.57)
                .setDescription("test description")
                .build());
  }
}
