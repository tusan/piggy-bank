package com.piggybank.security.token;

public interface TokenValidator {
  String validateAndGetIssuer(String token);
}
