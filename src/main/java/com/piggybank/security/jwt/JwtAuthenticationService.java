package com.piggybank.security.jwt;

import com.piggybank.config.FeatureFlags;
import com.piggybank.security.TokenAuthentication;
import com.piggybank.security.TokenValidator;
import com.piggybank.service.authentication.repository.JpaUserRepository;
import com.piggybank.service.authentication.repository.PiggyBankUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtAuthenticationService implements AuthenticationManager {
  private final TokenValidator tokenValidator;
  private final JpaUserRepository userRepository;
  private final FeatureFlags featureFlags;

  public JwtAuthenticationService(
      final TokenValidator tokenValidator,
      final JpaUserRepository userRepository,
      final FeatureFlags featureFlags) {
    this.tokenValidator = tokenValidator;
    this.userRepository = userRepository;
    this.featureFlags = featureFlags;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    final String token = authentication.getCredentials().toString();

    return validateAndRetrieve(token).map(TokenAuthentication::authorizedUser).orElse(null);
  }

  private Optional<PiggyBankUser> resolveUserByIssuer(String issuer) {
    return userRepository.findByUsername(issuer);
  }

  private Optional<PiggyBankUser> resolveUserByToken(String token) {
    return userRepository.findByToken(token);
  }

  private Optional<PiggyBankUser> validateAndRetrieve(String token) {
    if (featureFlags.useIssuerToResolveUser()) {
      return resolveUserByIssuer(tokenValidator.validateAndGetIssuer(token));
    } else {
      return tokenValidator.validate(token) ? resolveUserByToken(token) : Optional.empty();
    }
  }
}
