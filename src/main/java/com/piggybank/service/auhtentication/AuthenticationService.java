package com.piggybank.service.auhtentication;

import com.piggybank.service.auhtentication.repository.PiggyBankUser;

import java.util.Optional;

public interface AuthenticationService {
  Optional<PiggyBankUser> login(String username, String password);

  Optional<PiggyBankUser> authenticateByToken(String token);

  void logout(String username);
}
