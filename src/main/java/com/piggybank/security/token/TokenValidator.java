package com.piggybank.security.token;

public interface TokenValidator {
  boolean validate(String token);

  default String validateAndGetIssuer(String token) {
    throw new UnsupportedOperationException();
  }
}
