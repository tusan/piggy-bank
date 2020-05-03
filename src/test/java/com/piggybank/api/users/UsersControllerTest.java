package com.piggybank.api.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piggybank.service.users.UserService;
import com.piggybank.service.users.repository.PiggyBankUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.piggybank.service.users.repository.PiggyBankUser.forUsernameAndPassword;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(MockitoJUnitRunner.class)
public class UsersControllerTest {
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";

  @InjectMocks private UsersController sut;
  @Mock private UserService userService;
  private MockMvc mockMvc;

  @Before
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
    String requestBody =
        "{\n" + "    \"username\": \"username\",\n" + "    \"password\": \"password\"\n" + "}";

    mockMvc
        .perform(
            post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isCreated());

    PiggyBankUser addedUser = forUsernameAndPassword(USERNAME, PASSWORD);

    verify(userService).add(addedUser);
  }
}
