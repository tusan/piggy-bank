package com.piggybank.security.authentication.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggybank.security.authentication.AuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.piggybank.ServletStreamHelper.mockServletInputStream;
import static com.piggybank.security.authentication.LogoutDto.forUsername;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JwtLogoutHandlerTest {
  @InjectMocks private JwtLogoutHandler sut;

  @Spy private ObjectMapper objectMapper;

  @Mock private AuthenticationService authenticationService;

  @Mock private HttpServletResponse response;

  @Mock private HttpServletRequest request;

  @Test
  public void givenALoggedUserShouldRevokeTheAuthentication() throws Exception {
    final ServletInputStream mockServletInputStream =
        mockServletInputStream(forUsername("username"), objectMapper);

    when(request.getInputStream()).thenReturn(mockServletInputStream);

    sut.logout(request, response, null);

    verify(authenticationService).revoke("username");
  }
}
