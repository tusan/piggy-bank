package com.piggybank.security.jwt;

import com.piggybank.security.TokenBuilder;
import com.piggybank.security.TokenValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static java.time.temporal.ChronoUnit.DAYS;

public class JwtTokenHelper implements TokenBuilder, TokenValidator {
  public static final String ISSUER = "piggy-bank-app";
  private final JwtParser jwtParser;
  private final SecretKey key;
  private final InstantMarker instantMarker;

  public JwtTokenHelper(final InstantMarker instantMarker) {
    this.instantMarker = instantMarker;
    this.key = secretKeyFor(HS256);
    this.jwtParser = createJwtParser();
  }

  @Override
  public boolean validate(final String jws) {
    return ISSUER.equals(parse(jws).getBody().getIssuer());
  }

  @Override
  public String createNew() {
    return Jwts.builder()
        .signWith(key)
        .setExpiration(Date.from(instantMarker.getCurrent().plus(1, DAYS)))
        .setIssuer(ISSUER)
        .compact();
  }

  Jws<Claims> parse(final String jws) {
    return jwtParser.parseClaimsJws(jws);
  }

  private JwtParser createJwtParser() {
    return Jwts.parserBuilder().setSigningKey(key).build();
  }
}
