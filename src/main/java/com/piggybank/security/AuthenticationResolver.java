package com.piggybank.security;

import com.piggybank.service.auhtentication.repository.PiggyBankUser;

import java.util.Optional;

public interface AuthenticationResolver {
  Optional<PiggyBankUser> retrieveForToken(final String token);
}
