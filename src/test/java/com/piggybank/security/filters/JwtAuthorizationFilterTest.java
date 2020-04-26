package com.piggybank.security.filters;

import com.piggybank.security.AuthenticationResolver;
import com.piggybank.security.SecurityContextHolderFacade;
import com.piggybank.security.TokenAuthentication;
import com.piggybank.service.authentication.repository.PiggyBankUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.piggybank.security.RequestUtils.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JwtAuthorizationFilterTest {
  @InjectMocks JWTAuthorizationFilter sut;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @Mock private AuthenticationResolver authenticationResolver;

  @Mock private SecurityContextHolderFacade securityContextHolderFacade;

  @Mock private AuthenticationManager authenticationManager;
  public static final PiggyBankUser USER = new PiggyBankUser();
  public static final TokenAuthentication TOKEN_AUTHENTICATION =
      TokenAuthentication.authorizedUser(USER);

  @Before
  public void setUp() {
    when(authenticationResolver.retrieveForToken(anyString())).thenReturn(Optional.of(USER));
    when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer: token123");
  }

  @Test
  public void shouldAddTheAuthenticatedUserInSessionWhenValidJwtTokenIsInRequest()
      throws IOException, ServletException {
    sut.doFilterInternal(request, response, filterChain);

    verify(securityContextHolderFacade).setAuthentication(TOKEN_AUTHENTICATION);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void shouldNotModifySessionWhenInValidJwtTokenIsInRequest()
      throws IOException, ServletException {
    when(request.getHeader(AUTHORIZATION)).thenReturn(null);

    sut.doFilterInternal(request, response, filterChain);

    verifyNoInteractions(securityContextHolderFacade);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void shouldNotModifySessionWhenNoUserIsResolved() throws IOException, ServletException {
    when(authenticationResolver.retrieveForToken(anyString())).thenReturn(Optional.empty());

    sut.doFilterInternal(request, response, filterChain);

    verifyNoInteractions(securityContextHolderFacade);
    verify(filterChain).doFilter(request, response);
  }
}
