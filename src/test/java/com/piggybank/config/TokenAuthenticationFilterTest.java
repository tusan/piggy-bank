package com.piggybank.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class TokenAuthenticationFilterTest {
  private TokenAuthenticationFilter sut;

  private HttpServletRequest request;

  @Before
  public void setUp() {
    sut = new TokenAuthenticationFilter(AnyRequestMatcher.INSTANCE);
    request = Mockito.mock(HttpServletRequest.class);
  }

  @Test
  public void shouldReturnTheAuthenticationObjectIfTokenIsInRequestHeader() {
    sut.setAuthenticationManager(new MockedAuthenticationManager());
    Mockito.when(request.getHeader(Mockito.anyString())).thenReturn("Bearer token");

    Authentication auth = sut.attemptAuthentication(request, null);

    Assert.assertEquals("token", auth.getCredentials());
    Assert.assertEquals("token", auth.getPrincipal());
  }

  @Test(expected = BadCredentialsException.class)
  public void shouldThrowExceptionForMissingToken() {
    Mockito.when(request.getHeader(Mockito.anyString())).thenReturn(null);

    sut.attemptAuthentication(request, null);
  }

  class MockedAuthenticationManager implements AuthenticationManager {
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
      return auth;
    }
  }
}
