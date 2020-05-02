package com.piggybank.security.token;

public interface TokenValidator {
  String validateAndGetIssuer(final String token);
}
