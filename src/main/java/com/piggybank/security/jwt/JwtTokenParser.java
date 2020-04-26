package com.piggybank.security.jwt;

import com.piggybank.service.authentication.repository.PiggyBankUser;

public interface JwtTokenParser {
  PiggyBankUser resolveToken(String jws);
}
