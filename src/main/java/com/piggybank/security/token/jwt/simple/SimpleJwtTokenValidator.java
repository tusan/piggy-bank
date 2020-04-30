package com.piggybank.security.token.jwt.simple;

import com.piggybank.security.token.TokenValidator;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Qualifier;

import java.security.Key;

import static com.piggybank.config.Environment.ISSUER;

public class SimpleJwtTokenValidator implements TokenValidator {
  private final JwtParser jwtParser;

  public SimpleJwtTokenValidator(@Qualifier("jwtKey") final Key securityKey) {
    this.jwtParser = Jwts.parserBuilder().setSigningKey(securityKey).build();
  }

  @Override
  public boolean validate(final String jws) {
    return ISSUER.equals(jwtParser.parseClaimsJws(jws).getBody().getIssuer());
  }
}
