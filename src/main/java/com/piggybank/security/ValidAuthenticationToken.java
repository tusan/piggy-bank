package com.piggybank.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static java.util.Collections.singleton;

public final class ValidAuthenticationToken extends AbstractAuthenticationToken {
  private static final GrantedAuthority USER_ROLE = new SimpleGrantedAuthority("USER");

  private final String token;
  private final String username;

  static ValidAuthenticationToken authorizedFromTokenAndUsername(
      final String token, final String username) {
    return new ValidAuthenticationToken(token, username);
  }

  private ValidAuthenticationToken(final String token, final String username) {
    super(singleton(USER_ROLE));
    this.token = token;
    this.username = username;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return token;
  }

  @Override
  public Object getPrincipal() {
    return username;
  }
}
