package com.piggybank.security.authentication.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggybank.security.authentication.AuthenticationService;
import com.piggybank.security.authentication.jwt.JwtAuthenticationFilter;
import com.piggybank.security.token.TokenAuthentication;
import com.piggybank.service.users.PiggyBankUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import javax.servlet.FilterChain;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static com.piggybank.security.authentication.LoggedUserDto.forUsernameAndToken;
import static com.piggybank.security.authentication.LoginRequestDto.forUsernameAndPassword;
import static com.piggybank.service.users.PiggyBankUser.forUsernamePasswordAndToken;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationFilterTest {
  public static final PiggyBankUser PIGGY_BANK_USER =
      forUsernamePasswordAndToken("username", "password", "token");
  @InjectMocks private JwtAuthenticationFilter sut;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private AuthenticationService authenticationService;

  @Mock private FilterChain chain;

  @Spy private ObjectMapper objectMapper;

  @Before
  public void setUp() {
    when(request.getContentLength()).thenReturn(100);
  }

  @Test
  public void givenAnAuthenticationCredentialsForARealsUserShouldAuthenticate() throws Exception {
    final ServletInputStream servletInputStream = mockRequestBody();
    when(request.getInputStream()).thenReturn(servletInputStream);

    when(authenticationService.authorize("username", "password"))
        .thenReturn(Optional.of(PIGGY_BANK_USER));

    final TokenAuthentication expected = TokenAuthentication.authorizedUser(PIGGY_BANK_USER);
    final Authentication actual = sut.attemptAuthentication(request, response);

    assertEquals(expected, actual);
  }

  @Test(expected = BadCredentialsException.class)
  public void shouldThrowBadCredentialExceptionForInvalidAuthentication() throws Exception {
    final ServletInputStream servletInputStream = mockRequestBody();
    when(request.getInputStream()).thenReturn(servletInputStream);

    when(authenticationService.authorize("username", "password")).thenReturn(Optional.empty());

    sut.attemptAuthentication(request, response);
  }

  @Test(expected = BadCredentialsException.class)
  public void shouldThrowBadCredentialExceptionForMissingAuthentication() {
    when(request.getContentLength()).thenReturn(0);

    sut.attemptAuthentication(request, response);
  }

  @Test
  public void shouldSetTheTokenInResponseForSuccessfulAuthentication() throws IOException {
    final ServletOutputStream servletOutputStream = Mockito.mock(ServletOutputStream.class);
    when(response.getOutputStream()).thenReturn(servletOutputStream);

    final TokenAuthentication authentication = TokenAuthentication.authorizedUser(PIGGY_BANK_USER);
    sut.successfulAuthentication(request, response, chain, authentication);

    final byte[] expected =
        objectMapper.writeValueAsBytes(forUsernameAndToken("username", "token"));

    verify(servletOutputStream).write(expected);
  }

  private ServletInputStream mockRequestBody() throws JsonProcessingException {
    return new DelegatingServletInputStream(
        new ByteArrayInputStream(
            objectMapper.writeValueAsBytes(forUsernameAndPassword("username", "password"))));
  }
}
