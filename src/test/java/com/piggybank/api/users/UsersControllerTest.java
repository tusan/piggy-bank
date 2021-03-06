package com.piggybank.api.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piggybank.service.users.AuthenticationService;
import com.piggybank.service.users.repository.PiggyBankUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.piggybank.service.users.repository.PiggyBankUser.forUsernameAndPassword;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
public class UsersControllerTest {
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";

  @InjectMocks private UsersController sut;
  @Mock private AuthenticationService authenticationService;
  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(sut)
            .setMessageConverters(
                new MappingJackson2HttpMessageConverter(
                    new ObjectMapper().registerModule(new JavaTimeModule())))
            .build();
  }

  @Test
  public void shouldReturnAcceptedWhenAddingNewUserProperly() throws Exception {
    final String requestBody =
        "{\n" + "    \"username\": \"username\",\n" + "    \"password\": \"password\"\n" + "}";

    mockMvc
        .perform(
            post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isCreated());

    final PiggyBankUser addedUser = forUsernameAndPassword(USERNAME, PASSWORD);

    verify(authenticationService).add(addedUser);
  }
}
