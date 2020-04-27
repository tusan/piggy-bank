package com.piggybank.security.jwt;

import com.piggybank.config.Environment;
import com.piggybank.security.TokenValidator;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

public final class JwtTokenValidator implements TokenValidator {
  private final JwtParser jwtParser;

  public JwtTokenValidator() {
    this.jwtParser = createJwtParser();
  }

  @Override
  public boolean validate(String token) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String validateAndGetIssuer(String token) {
    return jwtParser.parseClaimsJws(token).getBody().getIssuer();
  }

  private JwtParser createJwtParser() {
    return Jwts.parserBuilder().setSigningKey(Environment.SECRET_KEY).build();
  }
}
