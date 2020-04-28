package com.piggybank.security.token;

import com.piggybank.service.authentication.repository.PiggyBankUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static java.util.Collections.singleton;

public final class TokenAuthentication extends UsernamePasswordAuthenticationToken {
  private static final GrantedAuthority USER_ROLE = new SimpleGrantedAuthority("USER");

  private TokenAuthentication(
      final PiggyBankUser user,
      final String credentials,
      final Collection<GrantedAuthority> grantedAuthorities) {
    super(user, credentials, grantedAuthorities);
  }

  public static TokenAuthentication authorizedUser(final PiggyBankUser user) {
    return new TokenAuthentication(user, user.getToken(), singleton(USER_ROLE));
  }

  public static TokenAuthentication unauthorizedUser(final String token) {
    return new TokenAuthentication(null, token, null);
  }
}
