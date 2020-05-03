package com.piggybank.service.users;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
final class JpaUserService implements UserService {
  private final JpaUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  JpaUserService(final JpaUserRepository userRepository, final PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void addOrReplace(final PiggyBankUser addedUser) {
    addedUser.setPassword(passwordEncoder.encode(addedUser.getPassword()));
    userRepository.save(addedUser);
  }

  @Override
  public Optional<PiggyBankUser> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }
}
