package com.piggybank.security;

import com.piggybank.service.auhtentication.AuthenticationService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static com.piggybank.security.ValidAuthenticationToken.authorizedFromToken;

@Component
final class TokenAuthenticationProvider implements AuthenticationProvider {
  private final AuthenticationService authenticationService;

  public TokenAuthenticationProvider(final AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    return authenticationService
        .retrieveForToken(authentication.getCredentials().toString())
        .map(user -> authorizedFromToken(user.getToken(), user.getUsername()))
        .orElseThrow(() -> new UsernameNotFoundException("Invalid authentication"));
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (ValidAuthenticationToken.class.isAssignableFrom(authentication));
  }
}
