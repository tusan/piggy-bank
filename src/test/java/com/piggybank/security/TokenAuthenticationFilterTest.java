package com.piggybank.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.piggybank.security.ValidAuthenticationToken.unauthorizedFromToken;
import static com.piggybank.security.TokenAuthenticationFilter.AUTHORIZATION;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TokenAuthenticationFilterTest {
  @InjectMocks private TokenAuthenticationFilter sut;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private ISecurityContextHolder securityContextHolder;

  @Mock private SecurityContext securityContext;

  @Mock private AuthenticationProvider authenticationProvider;

  @Mock private Authentication authentication;

  @Mock private FilterChain filterChain;

  @Before
  public void setUp() {
    when(securityContextHolder.getContext()).thenReturn(securityContext);
  }

  @Test
  public void shouldSetTheAuthenticationObjectIfTokenIsInRequestHeader() throws Exception {
    when(authenticationProvider.authenticate(unauthorizedFromToken("a token")))
        .thenReturn(authentication);

    when(request.getHeader(AUTHORIZATION)).thenReturn("a token");

    sut.doFilterInternal(request, response, filterChain);

    verify(securityContext).setAuthentication(authentication);
    verify(filterChain).doFilter(request, response);
  }

  @Test(expected = BadCredentialsException.class)
  public void shouldThrowExceptionForMissingToken() throws Exception {
    when(request.getHeader(Mockito.anyString())).thenReturn(null);
    sut.doFilterInternal(request, response, filterChain);
  }
}
