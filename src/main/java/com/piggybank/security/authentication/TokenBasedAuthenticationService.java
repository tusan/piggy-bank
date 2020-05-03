package com.piggybank.security.authentication;

import com.piggybank.security.token.TokenBuilder;
import com.piggybank.service.users.UserService;
import com.piggybank.service.users.repository.JpaUserRepository;
import com.piggybank.service.users.repository.PiggyBankUser;
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

  private PiggyBankUser saveUserToken(PiggyBankUser user) {
    user.setToken(tokenBuilder.createNew(user.getUsername()));

    userRepository.save(user);
    return user;
  }
}
