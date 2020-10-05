package com.piggybank.security.authentication;

import com.piggybank.security.token.TokenAuthentication;
import com.piggybank.security.token.TokenValidator;
import com.piggybank.service.users.repository.JpaUserRepository;
import com.piggybank.service.users.repository.PiggyBankUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
final class JwtAuthenticationManager implements AuthenticationManager {
  private final TokenValidator tokenValidator;
  private final JpaUserRepository userRepository;

  public JwtAuthenticationManager(
      final TokenValidator tokenValidator, final JpaUserRepository userRepository) {
    this.tokenValidator = tokenValidator;
    this.userRepository = userRepository;
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
    return userRepository.findByUsername(issuer);
  }
}
