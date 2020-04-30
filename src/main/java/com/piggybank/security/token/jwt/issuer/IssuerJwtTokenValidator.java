package com.piggybank.security.token.jwt.issuer;

import com.piggybank.security.token.TokenValidator;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Qualifier;

import java.security.Key;

public final class IssuerJwtTokenValidator implements TokenValidator {
  private final JwtParser jwtParser;
  private final Key securityKey;

  public IssuerJwtTokenValidator(final @Qualifier("jwtKey") Key securityKey) {
    this.securityKey = securityKey;
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
    return Jwts.parserBuilder().setSigningKey(securityKey).build();
  }
}
