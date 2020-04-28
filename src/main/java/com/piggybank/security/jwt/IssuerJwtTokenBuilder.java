package com.piggybank.security.jwt;

import com.piggybank.config.Environment;
import com.piggybank.security.TokenBuilder;
import io.jsonwebtoken.Jwts;

import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

public final class IssuerJwtTokenBuilder implements TokenBuilder {
  private final InstantMarker instantMarker;

  public IssuerJwtTokenBuilder(InstantMarker instantMarker) {
    this.instantMarker = instantMarker;
  }

  @Override
  public String createNew() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String createNew(String issuer) {
      return Jwts.builder()
          .signWith(Environment.SECRET_KEY)
          .setExpiration(Date.from(instantMarker.getCurrent().plus(1, DAYS)))
          .setIssuer(issuer)
          .compact();
    }

  }
