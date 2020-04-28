package com.piggybank.security.token;

public interface TokenValidator {
  boolean validate(String token);

  default String validateAndGetIssuer(String token) {
    throw new UnsupportedOperationException();
  }

  TokenValidator DEFAULT = token -> token != null && !token.isEmpty() && token.trim().length() > 0;
}