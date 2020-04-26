package com.piggybank.service.authentication;

import com.piggybank.security.TokenBuilder;
import com.piggybank.service.authentication.repository.JpaUserRepository;
import com.piggybank.service.authentication.repository.PiggyBankUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
final class TokenBasedAuthenticationService implements AuthenticationService {
  private final JpaUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenBuilder tokenBuilder;

  public TokenBasedAuthenticationService(
      final JpaUserRepository userRepository,
      final PasswordEncoder passwordEncoder,
      final TokenBuilder tokenBuilder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenBuilder = tokenBuilder;
  }

  @Override
  public Optional<PiggyBankUser> authenticate(final String username, final String password) {
    return userRepository
        .findByUsername(username)
        .filter(user -> passwordEncoder.matches(password, user.getPassword()))
        .map(
            user -> {
              user.setToken(tokenBuilder.createNew());
              userRepository.save(user);
              return user;
            });
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
