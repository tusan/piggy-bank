package com.piggybank.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piggybank.service.expenses.ExpensesService;
import com.piggybank.service.expenses.dto.ExpenseType;
import com.piggybank.service.expenses.repository.Expense;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ExpensesControllerTest {
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final MappingJackson2HttpMessageConverter messageConverter =
      new MappingJackson2HttpMessageConverter(mapper);

  @InjectMocks private ExpensesController sut;

  @Mock private ExpensesService expenseRepository;

  @Mock private PrincipalProvider principalProvider;

  private MockMvc mockMvc;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(sut).setMessageConverters(messageConverter).build();

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
            ExpensesService.Query.builder("logger_user")
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
        .perform(post("/api/v1/expenses").contentType(APPLICATION_JSON).content(json))
        .andExpect(status().isCreated())
        .andReturn();

    verify(expenseRepository).save(fakeExpense(LocalDate.of(2018, 11, 7)));
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
        .save(any(Expense.class));

    mockMvc
        .perform(post("/api/v1/expenses").contentType(APPLICATION_JSON).content(json))
        .andExpect(status().isInternalServerError())
        .andReturn();

    verify(expenseRepository).save(fakeExpense(LocalDate.of(2018, 11, 7)));
  }

  private Expense fakeExpense(final LocalDate date) {
    final Expense expense = new Expense();
    expense.setType(ExpenseType.HOUSE);
    expense.setDescription("test description");
    expense.setDate(date);
    expense.setAmount(22.57);
    return expense;
  }

  static {
    mapper.registerModule(new JavaTimeModule());
  }
}