package com.piggybank.service.authentication;

import com.piggybank.service.authentication.repository.PiggyBankUser;

import java.util.Optional;

public interface AuthenticationService {
  Optional<PiggyBankUser> authenticate(String username, String password);

  void revoke(String username);

  void add(PiggyBankUser addedUser);
}
