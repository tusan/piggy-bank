package com.piggybank.security.token;

import java.util.UUID;

public interface TokenBuilder {
  String createNew();

  default String createNew(String issuer) {
    throw new UnsupportedOperationException();
  }

  TokenBuilder DEFAULT = () -> UUID.randomUUID().toString();
}
