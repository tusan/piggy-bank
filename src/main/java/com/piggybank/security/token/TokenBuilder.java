package com.piggybank.security.token;

public interface TokenBuilder {
  String createNew();

  default String createNew(String issuer) {
    throw new UnsupportedOperationException();
  }
}
