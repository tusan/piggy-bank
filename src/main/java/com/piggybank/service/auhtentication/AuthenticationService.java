package com.piggybank.service.auhtentication;

import com.piggybank.service.auhtentication.repository.PiggyBankUser;

import java.util.Optional;

public interface AuthenticationService {
  Optional<PiggyBankUser> authenticate(String username, String password);

  Optional<PiggyBankUser> retrieveForToken(String token);

  void revoke(String username);
}
