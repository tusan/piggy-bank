package com.piggybank.security;

import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static java.util.Collections.singleton;

public final class ValidAuthenticationToken extends AbstractAuthenticationToken {
  private static final GrantedAuthority USER_ROLE = new SimpleGrantedAuthority("USER");

  private final PiggyBankUser user;

  static ValidAuthenticationToken authorizedUser(final PiggyBankUser user) {
    return new ValidAuthenticationToken(user);
  }

  private ValidAuthenticationToken(final PiggyBankUser user) {
    super(singleton(USER_ROLE));
    this.user = user;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return user.getToken();
  }

  @Override
  public Object getPrincipal() {
    return user;
  }
}
