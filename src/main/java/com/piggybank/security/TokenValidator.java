package com.piggybank.security;

public interface TokenValidator {
  boolean validate(String token);

  TokenValidator DEFAULT = token -> token != null && !token.isEmpty() && token.trim().length() > 0;
}
