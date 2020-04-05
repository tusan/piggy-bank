package com.piggybank.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UnauthorizedAuthenticationToken extends AbstractAuthenticationToken {
  private final String token;

  public static UnauthorizedAuthenticationToken unauthorizedFromToken(final String token) {
    return new UnauthorizedAuthenticationToken(token);
  }

  private UnauthorizedAuthenticationToken(final String token) {
    super(null);
    setAuthenticated(false);
    this.token = token;
  }

  @Override
  public Object getCredentials() {
    return token;
  }

  @Override
  public Object getPrincipal() {
    return null;
  }
}
