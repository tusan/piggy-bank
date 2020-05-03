package com.piggybank.service.users;

import com.piggybank.service.users.repository.JpaUserRepository;
import com.piggybank.service.users.repository.PiggyBankUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
final class JpaUserService implements UserService {
  private final JpaUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  JpaUserService(JpaUserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void add(final PiggyBankUser addedUser) {
    addedUser.setPassword(passwordEncoder.encode(addedUser.getPassword()));
    userRepository.save(addedUser);
  }
}
