package com.piggybank.security;

import com.piggybank.service.auhtentication.AuthenticationService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
  private final AuthenticationService authenticationService;

  public TokenAuthenticationProvider(final AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @Override
  protected UserDetails retrieveUser(
      final String username, final UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {
    return Optional.ofNullable(authentication.getCredentials())
        .flatMap(token -> authenticationService
                    .authenticateByToken(String.valueOf(token))
                    .map(user -> User.builder()
                                .username(user.getUsername())
                                .password(user.getPassword())
                                .roles("user")
                                .build()))
        .orElseThrow(() -> new BadCredentialsException("Invalid authentication"));
  }

  @Override
  protected void additionalAuthenticationChecks(
      final UserDetails userDetails, final UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {}
}
