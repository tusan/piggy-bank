package com.piggybank.security.authentication;

import com.piggybank.security.SecurityContextHolderFacade;
import com.piggybank.security.token.TokenAuthentication;
import com.piggybank.service.users.repository.PiggyBankUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.piggybank.security.RequestUtils.AUTHORIZATION;
import static com.piggybank.service.users.repository.PiggyBankUser.forUsernameAndPassword;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JwtAuthorizationFilterTest {
  @InjectMocks private JwtAuthorizationFilter sut;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @Mock private SecurityContextHolderFacade securityContextHolderFacade;

  @Mock private AuthenticationManager authenticationManager;

  private static final PiggyBankUser USER = forUsernameAndPassword("username", "password");
  private static final TokenAuthentication TOKEN_AUTHENTICATION =
      TokenAuthentication.authorizedUser(USER);

  @Before
  public void setUp() {
    when(authenticationManager.authenticate(any(Authentication.class)))
        .thenReturn(TOKEN_AUTHENTICATION);
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
    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);

    sut.doFilterInternal(request, response, filterChain);

    verifyNoInteractions(securityContextHolderFacade);
    verify(filterChain).doFilter(request, response);
  }
}