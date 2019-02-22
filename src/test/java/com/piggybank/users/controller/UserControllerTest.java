package com.piggybank.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piggybank.model.JpaUserRepository;
import com.piggybank.users.dto.LoggedUser;
import com.piggybank.users.dto.User;
import com.piggybank.users.services.UserAuthenticationService;
import org.junit.Assert;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    private UserController sut;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(
                        new ObjectMapper().registerModule(new JavaTimeModule())))
                .build();
    }

    @Test
    public void shouldReturn200IfLoginSucceed() throws Exception {
        final String requestBody = "{\n" +
                "    \"username\": \"username\",\n" +
                "    \"password\": \"password\"\n" +
                "}";

        Mockito.when(userAuthenticationService.login(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(User
                        .newBuilder()
                        .setUsername("username")
                        .setPassword("password")
                        .setId(1L)
                        .setToken("token")
                        .build()));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Mockito.verify(userAuthenticationService).login("username", "password");

        LoggedUser actual = mapper.readValue(response.getResponse().getContentAsByteArray(), LoggedUser.class);
        LoggedUser expected = LoggedUser.forUsernameAndToken("username", "token");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldThrowBadCredentialExceptionIfLoginFails() throws Exception {
        Mockito.when(userAuthenticationService.login(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        final String requestBody = "{\n" +
                "    \"username\": \"username\",\n" +
                "    \"password\": \"password\"\n" +
                "}";

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andReturn();
            Assert.fail();
        } catch(Exception e) {
            Assert.assertTrue(e.getCause() instanceof BadCredentialsException);
        }
    }

    @Test
    public void shouldReturnAcceptedWhenAddingNewUserProperly() throws Exception {
        String requestBody = "{\n" +
                "    \"username\": \"username\",\n" +
                "    \"password\": \"password\"\n" +
                "}";

        Mockito.when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn("encoded_password");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        com.piggybank.model.User expectedUser = new com.piggybank.model.User();
        expectedUser.setUsername("username");
        expectedUser.setPassword("encoded_password");

        Mockito.verify(userRepository).save(expectedUser);
    }
}