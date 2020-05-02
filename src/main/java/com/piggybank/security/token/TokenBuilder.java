package com.piggybank.security.token;

public interface TokenBuilder {
  String createNew(final String issuer);
}
