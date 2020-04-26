package com.piggybank.security.jwt;

public interface JwtTokenParser {
  String resolveToken(final String jws);
}
