package com.piggybank.security.jwt;

import com.piggybank.config.Environment;
import com.piggybank.security.TokenBuilder;
import com.piggybank.security.TokenValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import java.util.Date;

import static com.piggybank.config.Environment.SECRET_KEY;
import static java.time.temporal.ChronoUnit.DAYS;

public class JwtTokenHelper implements TokenBuilder, TokenValidator {
  private final JwtParser jwtParser;
  private final InstantMarker instantMarker;

  public JwtTokenHelper(final InstantMarker instantMarker) {
    this.instantMarker = instantMarker;
    this.jwtParser = createJwtParser();
  }

  @Override
  public boolean validate(final String jws) {
    return Environment.ISSUER.equals(parse(jws).getBody().getIssuer());
  }

  @Override
  public String createNew() {
    return Jwts.builder()
        .signWith(SECRET_KEY)
        .setExpiration(Date.from(instantMarker.getCurrent().plus(1, DAYS)))
        .setIssuer(Environment.ISSUER)
        .compact();
  }

  Jws<Claims> parse(final String jws) {
    return jwtParser.parseClaimsJws(jws);
  }

  private JwtParser createJwtParser() {
    return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build();
  }
}
