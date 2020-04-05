package com.piggybank.security;

import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static java.util.Collections.singleton;

final class BearerTokenAuthentication extends AbstractAuthenticationToken {
  private static final GrantedAuthority USER_ROLE = new SimpleGrantedAuthority("USER");

  private final PiggyBankUser user;

  static BearerTokenAuthentication authorizedUser(final PiggyBankUser user) {
    return new BearerTokenAuthentication(user);
  }

  private BearerTokenAuthentication(final PiggyBankUser user) {
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
