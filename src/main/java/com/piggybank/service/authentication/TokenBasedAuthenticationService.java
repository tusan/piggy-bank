package com.piggybank.service.authentication;

import com.piggybank.security.AuthenticationResolver;
import com.piggybank.security.TokenGenerator;
import com.piggybank.service.authentication.repository.JpaUserRepository;
import com.piggybank.service.authentication.repository.PiggyBankUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
final class TokenBasedAuthenticationService
    implements AuthenticationService, AuthenticationResolver {
  private final JpaUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenGenerator tokenGenerator;

  public TokenBasedAuthenticationService(
      final JpaUserRepository userRepository,
      final PasswordEncoder passwordEncoder,
      final TokenGenerator tokenGenerator) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  public Optional<PiggyBankUser> authenticate(final String username, final String password) {
    return userRepository
        .findByUsername(username)
        .filter(user -> passwordEncoder.matches(password, user.getPassword()))
        .map(
            user -> {
              user.setToken(tokenGenerator.newToken());
              userRepository.save(user);
              return user;
            });
  }

  @Override
  public Optional<PiggyBankUser> retrieveForToken(final String token) {
    return userRepository.findByToken(token);
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
}
