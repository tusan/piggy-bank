package com.piggybank.security.token.jwt.simple;

import com.piggybank.config.Environment;
import com.piggybank.security.InstantMarker;
import com.piggybank.security.token.TokenBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Qualifier;

import java.security.Key;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

public class SimpleJwtTokenBuilder implements TokenBuilder {
  private final InstantMarker instantMarker;
  private final Key securityKey;

  public SimpleJwtTokenBuilder(final InstantMarker instantMarker,
                               @Qualifier("jwtKey") final Key securityKey) {
    this.instantMarker = instantMarker;
    this.securityKey = securityKey;
  }

  @Override
  public String createNew() {
    return Jwts.builder()
        .signWith(securityKey)
        .setExpiration(Date.from(instantMarker.getCurrent().plus(1, DAYS)))
        .setIssuer(Environment.ISSUER)
        .compact();
  }
}
