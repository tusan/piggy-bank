package com.piggybank.security;

import com.piggybank.service.auhtentication.repository.PiggyBankUser;

public interface AuthenticationResolver {
  PiggyBankUser getLoggedUser(final String username);
}
