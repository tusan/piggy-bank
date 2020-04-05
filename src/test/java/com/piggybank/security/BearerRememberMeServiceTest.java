package com.piggybank.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.piggybank.security.BearerRememberMeService.AUTHORIZATION;
import static com.piggybank.security.UnauthorizedAuthenticationToken.unauthorizedFromToken;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BearerRememberMeServiceTest {
  @InjectMocks private BearerRememberMeService sut;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private AuthenticationProvider authenticationProvider;

  @Mock private Authentication authentication;

  @Test
  public void shouldReturnTheAuthenticationObjectIfTokenIsValidAndInRequestHeader() {
    when(authenticationProvider.authenticate(unauthorizedFromToken("a token")))
        .thenReturn(authentication);

    when(request.getHeader(AUTHORIZATION)).thenReturn("a token");

    assertEquals(authentication, sut.autoLogin(request, response));
  }

  @Test
  public void shouldReturnNullForMissingToken() throws Exception {
    when(request.getHeader(AUTHORIZATION)).thenReturn(null);

    assertNull(sut.autoLogin(request, response));

    verifyNoInteractions(authenticationProvider);
  }
}
