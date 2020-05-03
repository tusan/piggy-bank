package com.piggybank.security.authentication;

import com.piggybank.service.users.repository.PiggyBankUser;

import java.util.Optional;

public interface AuthenticationService {
  Optional<PiggyBankUser> authenticate(String username, String password);

  void revoke(String username);
}
