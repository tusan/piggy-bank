package com.piggybank.security.authentication;

import com.piggybank.security.token.TokenBuilder;
import com.piggybank.service.users.UserService;
import com.piggybank.service.users.PiggyBankUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
final class TokenAuthenticationService implements AuthenticationService {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final TokenBuilder tokenBuilder;

  public TokenAuthenticationService(
          final UserService userService,
          final PasswordEncoder passwordEncoder,
          final TokenBuilder tokenBuilder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.tokenBuilder = tokenBuilder;
  }

  @Override
  public Optional<PiggyBankUser> authorize(final String username, final String password) {
    return userService
        .findByUsername(username)
        .filter(user -> passwordEncoder.matches(password, user.getPassword()))
        .map(this::saveUserToken);
  }

  @Override
  public void revoke(final String username) {
    userService
        .findByUsername(username)
        .ifPresent(
            user -> {
              user.setToken(null);
              userService.addOrReplace(user);
            });
  }

  private PiggyBankUser saveUserToken(final PiggyBankUser user) {
    user.setToken(tokenBuilder.createNew(user.getUsername()));

    userService.addOrReplace(user);
    return user;
  }
}
