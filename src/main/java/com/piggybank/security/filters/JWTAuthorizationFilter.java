package com.piggybank.security.filters;

import com.piggybank.security.AuthenticationResolver;
import com.piggybank.security.SecurityContextHolderFacade;
import com.piggybank.security.TokenAuthentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.piggybank.security.RequestUtils.extractBearerToken;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
  private final AuthenticationResolver authenticationResolver;
  private final SecurityContextHolderFacade securityContextHolderFacade;

  public JWTAuthorizationFilter(
      final AuthenticationManager authenticationManager,
      final AuthenticationResolver authenticationResolver,
      final SecurityContextHolderFacade securityContextHolderFacade) {
    super(authenticationManager);
    this.authenticationResolver = authenticationResolver;
    this.securityContextHolderFacade = securityContextHolderFacade;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    extractBearerToken(request)
        .flatMap(authenticationResolver::retrieveForToken)
        .map(TokenAuthentication::authorizedUser)
        .ifPresent(securityContextHolderFacade::setAuthentication);

    chain.doFilter(request, response);
  }
}
