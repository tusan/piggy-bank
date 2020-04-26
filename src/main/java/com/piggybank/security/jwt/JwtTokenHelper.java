package com.piggybank.security.jwt;

import com.piggybank.security.TokenBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static java.time.temporal.ChronoUnit.DAYS;

class JwtTokenHelper implements TokenBuilder, JwtTokenParser {
  private final JwtParser jwtParser;
  private final SecretKey key;
  private final InstantMarker instantMarker;

  public JwtTokenHelper(final InstantMarker instantMarker) {
    this.instantMarker = instantMarker;
    this.key = secretKeyFor(HS256);
    this.jwtParser = createJwtParser();
  }

  @Override
  public String resolveToken(final String jws) {
    return parse(jws).getBody().getIssuer();
  }

  @Override
  public String createNew() {
    return Jwts.builder()
        .signWith(key)
        .setExpiration(Date.from(instantMarker.getCurrent().plus(1, DAYS)))
        .setIssuer("piggy-bank-app")
        .compact();
  }

  Jws<Claims> parse(final String jws) {
    return jwtParser.parseClaimsJws(jws);
  }

  private JwtParser createJwtParser() {
    return Jwts.parserBuilder().setSigningKey(key).build();
  }
}
