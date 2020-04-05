package com.piggybank.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static com.piggybank.security.ValidAuthenticationToken.authorizedFromTokenAndUsername;

@Component
final class TokenAuthenticationProvider implements AuthenticationProvider {
  private final AuthenticationResolver authenticationService;

  public TokenAuthenticationProvider(final AuthenticationResolver authenticationService) {
    this.authenticationService = authenticationService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    return authenticationService
        .retrieveForToken(authentication.getCredentials().toString())
        .map(user -> authorizedFromTokenAndUsername(user.getToken(), user.getUsername()))
        .orElseThrow(() -> new UsernameNotFoundException("Invalid authentication"));
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (ValidAuthenticationToken.class.isAssignableFrom(authentication));
  }
}
