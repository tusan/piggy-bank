package com.piggybank.security.jwt;

import com.piggybank.config.Environment;
import com.piggybank.security.TokenBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

public class SimpleJwtTokenBuilder implements TokenBuilder {
  private final InstantMarker instantMarker;

  public SimpleJwtTokenBuilder(final InstantMarker instantMarker) {
    this.instantMarker = instantMarker;
  }

  @Override
  public String createNew() {
    return Jwts.builder()
        .signWith(Environment.SECRET_KEY)
        .setExpiration(Date.from(instantMarker.getCurrent().plus(1, DAYS)))
        .setIssuer(Environment.ISSUER)
        .compact();
  }
}
