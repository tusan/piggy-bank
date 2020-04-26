package com.piggybank.security;

import com.piggybank.service.authentication.repository.JpaUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthenticationService implements AuthenticationManager {
  private final TokenValidator tokenValidator;
  private final JpaUserRepository userRepository;

  public JwtAuthenticationService(
      final TokenValidator tokenValidator, final JpaUserRepository userRepository) {
    this.tokenValidator = tokenValidator;
    this.userRepository = userRepository;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    final String token = authentication.getCredentials().toString();
    if (!tokenValidator.validate(token)) {
      return null;
    }

    return userRepository
        .findByToken(token)
        .map(TokenAuthentication::authorizedUser)
        .orElse(null);
  }
}
