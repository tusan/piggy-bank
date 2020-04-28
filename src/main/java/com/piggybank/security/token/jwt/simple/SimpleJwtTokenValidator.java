package com.piggybank.security.token.jwt.simple;

import com.piggybank.security.token.TokenValidator;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import static com.piggybank.config.Environment.ISSUER;
import static com.piggybank.config.Environment.SECRET_KEY;

public class SimpleJwtTokenValidator implements TokenValidator {
  private final JwtParser jwtParser;

  public SimpleJwtTokenValidator() {
    this.jwtParser = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build();
  }

  @Override
  public boolean validate(final String jws) {
    return ISSUER.equals(jwtParser.parseClaimsJws(jws).getBody().getIssuer());
  }
}
