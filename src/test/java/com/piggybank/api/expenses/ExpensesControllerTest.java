package com.piggybank.api.expenses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piggybank.service.expenses.ExpensesService;
import com.piggybank.service.expenses.repository.Expense;
import com.piggybank.service.users.repository.PiggyBankUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static com.piggybank.service.users.repository.PiggyBankUser.forUsernameAndPassword;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ExpensesControllerTest {
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final MappingJackson2HttpMessageConverter messageConverter =
      new MappingJackson2HttpMessageConverter(mapper);
  private static final PiggyBankUser LOGGER_USER = forUsernameAndPassword("username", "password");

  static {
    mapper.registerModule(new JavaTimeModule());
  }

  @InjectMocks private ExpensesController sut;
  @Mock private ExpensesService expenseRepository;
  @Mock private Authentication principal;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(sut).setMessageConverters(messageConverter).build();

    when(principal.getPrincipal()).thenReturn(LOGGER_USER);
  }

  @Test
  public void shouldReturn200AndTheTransferObjectsInDateRange() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/expenses")
                .param("date-start", "2018-11-07")
                .param("date-end", "2018-12-07")
                .principal(principal))
        .andExpect(status().isOk())
        .andReturn();

    verify(expenseRepository)
        .find(
            ExpensesService.Query.builder(LOGGER_USER)
                .setDateStart(LocalDate.of(2018, 11, 7))
                .setDateEnd(LocalDate.of(2018, 12, 7))
                .build());
  }

  @Test
  public void shouldReturn201AndCallSaveOnRepository() throws Exception {
    final String json =
        "{\n"
            + "  \"date\": \"2018-11-07\",\n"
            + "  \"type\": \"HOUSE\",\n"
            + "  \"amount\": \"22.57\",\n"
            + "  \"description\": \"test description\"\n"
            + "}";

    mockMvc
        .perform(
            post("/api/v1/expenses")
                .contentType(APPLICATION_JSON)
                .content(json)
                .principal(principal))
        .andExpect(status().isCreated())
        .andReturn();

    verify(expenseRepository).save(fakeExpense(LocalDate.of(2018, 11, 7)));
  }

  @Test
  public void shouldReturnInternalServerErrorWhenRepositoryFails() throws Exception {
    final String json =
        "{\n"
            + "  \"date\": \"2018-11-07\",\n"
            + "  \"type\": \"HOUSE\",\n"
            + "  \"amount\": \"22.57\",\n"
            + "  \"description\": \"test description\"\n"
            + "}";

    doThrow(new RuntimeException("test exception"))
        .when(expenseRepository)
        .save(any(Expense.class));

    mockMvc
        .perform(
            post("/api/v1/expenses")
                .contentType(APPLICATION_JSON)
                .content(json)
                .principal(principal))
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
    expense.setOwner(LOGGER_USER);
    return expense;
  }
}
