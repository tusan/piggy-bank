package com.piggybank.security;

import com.piggybank.service.authentication.repository.PiggyBankUser;

import java.util.Optional;

@Deprecated
public interface AuthenticationResolver {
  Optional<PiggyBankUser> retrieveForToken(final String token);
}
