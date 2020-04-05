package com.piggybank.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
  static final String AUTHORIZATION = "Authorization";
  static final String BEARER = "Bearer";

  private static final Logger LOGGER = Logger.getLogger(TokenAuthenticationFilter.class.getName());

  private final AuthenticationProvider authenticationProvider;
  private final ISecurityContextHolder securityContextHolder;

  public TokenAuthenticationFilter(
      final AuthenticationProvider authenticationProvider,
      final ISecurityContextHolder securityContextHolder) {
    this.authenticationProvider = authenticationProvider;
    this.securityContextHolder = securityContextHolder;
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    final String token = resolveAuthenticationToken(request);

    final Authentication auth =
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(token, token));

    LOGGER.info(String.format("User properly logged. [user=%s]", auth.getPrincipal()));

    securityContextHolder.getContext().setAuthentication(auth);
    filterChain.doFilter(request, response);
  }

  private String resolveAuthenticationToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(AUTHORIZATION))
        .map(v -> v.replace(BEARER, "").trim())
        .orElseThrow(() -> new BadCredentialsException("Missing authentication token."));
  }
}
