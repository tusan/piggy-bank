package com.piggybank.security.authentication.jwt;

import com.piggybank.security.token.TokenAuthentication;
import com.piggybank.security.token.TokenValidator;
import com.piggybank.service.users.UserService;
import com.piggybank.service.users.PiggyBankUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
final class JwtAuthenticationManager implements AuthenticationManager {
  private final TokenValidator tokenValidator;
  private final UserService userService;

  public JwtAuthenticationManager(
          final TokenValidator tokenValidator, final UserService userService) {
    this.tokenValidator = tokenValidator;
    this.userService = userService;
  }

  @Override
  public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
    final String token = authentication.getCredentials().toString();
    return validateAndRetrieve(token).map(TokenAuthentication::authorizedUser).orElse(null);
  }

  private Optional<PiggyBankUser> validateAndRetrieve(final String token) {
    return resolveUserByIssuer(tokenValidator.validateAndGetIssuer(token))
        .filter(user -> token.equals(user.getToken()));
  }

  private Optional<PiggyBankUser> resolveUserByIssuer(final String issuer) {
    return userService.findByUsername(issuer);
  }
}
