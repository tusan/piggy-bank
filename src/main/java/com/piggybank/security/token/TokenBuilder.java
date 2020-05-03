package com.piggybank.security.token;

public interface TokenBuilder {
  String createNew(String issuer);
}
