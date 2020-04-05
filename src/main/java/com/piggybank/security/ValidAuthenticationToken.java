package com.piggybank.security;

import com.google.common.base.MoreObjects;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Objects;

import static java.util.Collections.singleton;

public final class ValidAuthenticationToken extends AbstractAuthenticationToken {
  private static final GrantedAuthority USER_ROLE = new SimpleGrantedAuthority("USER");

  private final String token;
  private final String username;

  private ValidAuthenticationToken(
      final String token,
      final String username,
      final boolean authenticated,
      final Collection<GrantedAuthority> grantedAuthorities) {
    super(grantedAuthorities);
    this.token = token;
    this.username = username;
    this.setAuthenticated(authenticated);
  }

  public static ValidAuthenticationToken unauthorizedFromToken(final String token) {
    return new ValidAuthenticationToken(token, null, false, null);
  }

  static ValidAuthenticationToken authorizedFromToken(final String token, final String username) {
    return new ValidAuthenticationToken(token, username, true, singleton(USER_ROLE));
  }

  @Override
  public Object getCredentials() {
    return token;
  }

  @Override
  public Object getPrincipal() {
    return username;
  }

  @Override
  public boolean isAuthenticated() {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ValidAuthenticationToken that = (ValidAuthenticationToken) o;
    return Objects.equals(token, that.token) && Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), token, username);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("token", token)
        .add("username", username)
        .toString();
  }
}
