package com.piggybank.security.jwt;

import com.piggybank.security.TokenBuilder;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static java.time.temporal.ChronoUnit.DAYS;

public final class JwtTokenBuilderImpl implements TokenBuilder {
  static final Key SECRET_KEY = secretKeyFor(HS256);
  private final InstantMarker instantMarker;

  private JwtTokenBuilderImpl(InstantMarker instantMarker) {
    this.instantMarker = instantMarker;
  }

  @Override
  public String createNew() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String createNew(String issuer) {
      return Jwts.builder()
          .signWith(SECRET_KEY)
          .setExpiration(Date.from(instantMarker.getCurrent().plus(1, DAYS)))
          .setIssuer(issuer)
          .compact();
    }

  }
