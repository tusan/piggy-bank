package com.piggybank.security.authentication;

import com.piggybank.security.SecurityContextHolderFacade;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.piggybank.security.RequestUtils.extractBearerToken;
import static com.piggybank.security.token.TokenAuthentication.unauthorizedUser;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
  private final SecurityContextHolderFacade securityContextHolderFacade;
  private final AuthenticationManager authenticationManager;

  public JwtAuthorizationFilter(
      final AuthenticationManager authenticationManager,
      final SecurityContextHolderFacade securityContextHolderFacade) {
    this.authenticationManager = authenticationManager;
    this.securityContextHolderFacade = securityContextHolderFacade;
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
      throws IOException, ServletException {

    extractBearerToken(request)
        .flatMap(token -> Optional.of(unauthorizedUser(token)))
        .flatMap(
            authentication ->
                Optional.ofNullable(authenticationManager.authenticate(authentication)))
        .ifPresent(securityContextHolderFacade::setAuthentication);

    chain.doFilter(request, response);
  }
}
