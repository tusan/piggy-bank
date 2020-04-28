package com.piggybank.security.filters;

import com.piggybank.security.SecurityContextHolderFacade;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.piggybank.security.RequestUtils.extractBearerToken;
import static com.piggybank.security.token.TokenAuthentication.unauthorizedUser;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
  private final SecurityContextHolderFacade securityContextHolderFacade;

  public JwtAuthorizationFilter(
      final AuthenticationManager authenticationManager,
      final SecurityContextHolderFacade securityContextHolderFacade) {
    super(authenticationManager);
    this.securityContextHolderFacade = securityContextHolderFacade;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    extractBearerToken(request)
        .flatMap(token -> Optional.of(unauthorizedUser(token)))
        .flatMap(authentication -> Optional.ofNullable(getAuthenticationManager().authenticate(authentication)))
        .ifPresent(securityContextHolderFacade::setAuthentication);

    chain.doFilter(request, response);
  }
}
