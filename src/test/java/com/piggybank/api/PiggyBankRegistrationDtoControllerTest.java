package com.piggybank.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piggybank.service.auhtentication.AuthenticationService;
import com.piggybank.service.auhtentication.dto.LoggedUserDto;
import com.piggybank.service.auhtentication.repository.JpaUserRepository;
import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(MockitoJUnitRunner.class)
public class PiggyBankRegistrationDtoControllerTest {

  @InjectMocks private AuthenticationController sut;

  @Mock private AuthenticationService authenticationService;

  @Mock private JpaUserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  private MockMvc mockMvc;

  private ObjectMapper mapper = new ObjectMapper();

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
  public void shouldReturn200IfLoginSucceed() throws Exception {
    final PiggyBankUser piggyBankUser = new PiggyBankUser();
    piggyBankUser.setUsername("username");
    piggyBankUser.setPassword("password");
    piggyBankUser.setToken("token");

    final String requestBody =
        "{\n" + "    \"username\": \"username\",\n" + "    \"password\": \"password\"\n" + "}";

    when(authenticationService.login(anyString(), anyString()))
        .thenReturn(Optional.of(piggyBankUser));

    final MvcResult response =
        mockMvc
            .perform(
                post("/api/v1/users/authorize")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    verify(authenticationService).login("username", "password");

    final LoggedUserDto actual =
        mapper.readValue(response.getResponse().getContentAsByteArray(), LoggedUserDto.class);
    final LoggedUserDto expected = LoggedUserDto.forUsernameAndToken("username", "token");

    assertEquals(expected, actual);
  }

  @Test
  public void shouldThrowBadCredentialExceptionIfLoginFails() {
    when(authenticationService.login(anyString(), anyString())).thenReturn(Optional.empty());

    final String requestBody =
        "{\n" + "    \"username\": \"username\",\n" + "    \"password\": \"password\"\n" + "}";

    try {
      mockMvc
          .perform(
              post("/api/v1/users/authorize")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody))
          .andReturn();
      fail();
    } catch (Exception e) {
      assertTrue(e.getCause() instanceof BadCredentialsException);
    }
  }

  @Test
  public void shouldReturnAcceptedWhenAddingNewUserProperly() throws Exception {
    String requestBody =
        "{\n" + "    \"username\": \"username\",\n" + "    \"password\": \"password\"\n" + "}";

    when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");

    mockMvc
        .perform(
            post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(MockMvcResultMatchers.status().isCreated());

    PiggyBankUser expectedPiggyBankUser = new PiggyBankUser();
    expectedPiggyBankUser.setUsername("username");
    expectedPiggyBankUser.setPassword("encoded_password");

    verify(userRepository).save(expectedPiggyBankUser);
  }
}
