package com.piggybank.security;

import com.google.common.base.MoreObjects;
import com.piggybank.service.authentication.repository.PiggyBankUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Objects;

import static java.util.Collections.singleton;

final class BearerTokenAuthentication implements Authentication {
  private static final GrantedAuthority USER_ROLE = new SimpleGrantedAuthority("USER");

  private final PiggyBankUser user;

  private BearerTokenAuthentication(final PiggyBankUser user) {
    this.user = user;
  }

  static BearerTokenAuthentication authorizedUser(final PiggyBankUser user) {
    return new BearerTokenAuthentication(user);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return singleton(USER_ROLE);
  }

  @Override
  public Object getCredentials() {
    return user.getToken();
  }

  @Override
  public Object getDetails() {
    return user;
  }

  @Override
  public Object getPrincipal() {
    return user;
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

  @Override
  public String getName() {
    return user.getUsername();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BearerTokenAuthentication that = (BearerTokenAuthentication) o;
    return Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("user", user).toString();
  }
}
