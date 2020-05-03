package com.piggybank.security.authentication;

import com.piggybank.service.users.PiggyBankUser;

import java.util.Optional;

public interface AuthenticationService {
  Optional<PiggyBankUser> authorize(String username, String password);

  void revoke(String username);
}
