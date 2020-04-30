package com.piggybank.service.users;

import com.piggybank.config.FeatureFlags;
import com.piggybank.security.token.TokenBuilder;
import com.piggybank.service.users.repository.JpaUserRepository;
import com.piggybank.service.users.repository.PiggyBankUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

@Service
final class TokenBasedAuthenticationService implements AuthenticationService {
  private final JpaUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenBuilder tokenBuilder;
  private final FeatureFlags featureFlags;

  public TokenBasedAuthenticationService(
      final JpaUserRepository userRepository,
      final PasswordEncoder passwordEncoder,
      final TokenBuilder tokenBuilder,
      final FeatureFlags featureFlags) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenBuilder = tokenBuilder;
    this.featureFlags = featureFlags;
  }

  @Override
  public Optional<PiggyBankUser> authenticate(final String username, final String password) {
    return userRepository
        .findByUsername(username)
        .filter(user -> passwordEncoder.matches(password, user.getPassword()))
        .map(this::saveUserToken);
  }

  @Override
  public void revoke(final String username) {
    userRepository
        .findByUsername(username)
        .ifPresent(
            user -> {
              user.setToken(null);
              userRepository.save(user);
            });
  }

  @Override
  public void add(final PiggyBankUser addedUser) {
    addedUser.setPassword(passwordEncoder.encode(addedUser.getPassword()));
    userRepository.save(addedUser);
  }

  private PiggyBankUser saveUserToken(PiggyBankUser user) {
    if (featureFlags.useIssuerToResolveUser()) {
      user.setToken(tokenBuilder.createNew(user.getUsername()));
    } else {
      user.setToken(tokenBuilder.createNew());
    }

    userRepository.save(user);
    return user;
  }
}
