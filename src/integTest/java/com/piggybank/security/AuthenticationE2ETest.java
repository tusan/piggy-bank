package com.piggybank.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.piggybank.api.expenses.ExpenseDto;
import com.piggybank.api.expenses.ExpenseType;
import com.piggybank.security.authentication.LoggedUserDto;
import com.piggybank.security.authentication.LoginRequestDto;
import com.piggybank.security.authentication.LogoutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("integration")
public class AuthenticationE2ETest {
  public static final Random RANDOM_GENERATOR = new Random();
  private static final Logger LOGGER = Logger.getLogger(AuthenticationE2ETest.class.getName());
  @Autowired private WebApplicationContext context;
  @Autowired private ObjectMapper objectMapper;
  private MockMvc mvc;

  @BeforeEach
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  public void shouldReturnForbiddenForUnauthenticatedUsers() throws Exception {
    shouldBeNotAuthorized();
  }

  @Test
  public void shouldReturn200ForAuthenticatedUser() throws Exception {
    final String token = givenAnAuthenticationTokenForALoggedUser();
    whenRequestingAllUserExpenses(token);

    final List<ExpenseDto> expected = givenAListOfExpensesForTheGivenAuthenticatedUser(token);

    final List<ExpenseDto> result = whenRequestingAllUserExpenses(token);

    assertEquals(expected, result);
  }

  @Test
  public void shouldLogoutALoggedUser() throws Exception {
    final String token = givenAnAuthenticationTokenForALoggedUser();

    thenLogoutTheUser();

    shouldBeNotAuthorized(token);
  }

  private void shouldBeNotAuthorized(final String token) throws Exception {
    mvc.perform(
            get("/api/v1/expenses")
                .header("Authorization", String.format("Bearer %s", token))
                .contentType(APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  private void shouldBeNotAuthorized() throws Exception {
    mvc.perform(get("/api/v1/expenses").contentType(APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  private List<ExpenseDto> whenRequestingAllUserExpenses(final String token) throws Exception {
    final byte[] expensesResponse =
        mvc.perform(
                get("/api/v1/expenses")
                    .header("Authorization", String.format("Bearer %s", token))
                    .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

    return objectMapper.readValue(
        expensesResponse,
        TypeFactory.defaultInstance().constructCollectionType(List.class, ExpenseDto.class));
  }

  private List<ExpenseDto> givenAListOfExpensesForTheGivenAuthenticatedUser(final String token) {
    //noinspection ConstantConditions
    return RANDOM_GENERATOR
        .ints(0, 100)
        .mapToObj(
            i ->
                ExpenseDto.newBuilder()
                    .setAmount(1000d / i * 100)
                    .setType(ExpenseType.BANK_ACCOUNT)
                    .setDate(LocalDate.now())
                    .setDescription("Description n." + i)
                    .build())
        .limit(10)
        .peek(expenseDto -> makeExpenseCreateRequest(token, expenseDto))
        .collect(Collectors.toList());
  }

  private void makeExpenseCreateRequest(final String token, final ExpenseDto expenseDto) {
    try {
      mvc.perform(
              post("/api/v1/expenses")
                  .header("Authorization", String.format("Bearer %s", token))
                  .contentType(APPLICATION_JSON)
                  .content(objectMapper.writeValueAsBytes(expenseDto)))
          .andExpect(status().isCreated());
    } catch (final Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  private String givenAnAuthenticationTokenForALoggedUser() throws Exception {
    final LoginRequestDto loginRequest =
        LoginRequestDto.forUsernameAndPassword("username", "password");

    LOGGER.info(String.format("Authenticating User [loginRequest=%s]", loginRequest));
    final byte[] authResponse =
        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/authorize")
                    .content(objectMapper.writeValueAsString(loginRequest))
                    .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

    return objectMapper.readValue(authResponse, LoggedUserDto.class).token();
  }

  private void thenLogoutTheUser() throws Exception {
    final LogoutDto logoutDto = LogoutDto.forUsername("username");

    LOGGER.info(String.format("Revoke authentication for user [logoutRequest=%s]", logoutDto));
    mvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/revoke")
                .content(objectMapper.writeValueAsString(logoutDto))
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }
}
