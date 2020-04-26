package com.piggybank.security.jwt;

import com.piggybank.service.authentication.repository.PiggyBankUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.lang.Maps;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static java.time.temporal.ChronoUnit.DAYS;

class JwtTokenHelper implements JwtTokenBuilder, JwtTokenParser {
  public static final JacksonDeserializer JACKSON_DESERIALIZER =
      new JacksonDeserializer(Maps.of("user", PiggyBankUser.class).build());

  public static final JacksonSerializer<Map<String, ?>> JACKSON_SERIALIZER =
      new JacksonSerializer<>();

  private final JwtParser jwtParser;
  private final SecretKey key;
  private final InstantMarker instantMarker;

  public JwtTokenHelper(final InstantMarker instantMarker) {
    this.instantMarker = instantMarker;
    this.key = secretKeyFor(HS256);
    this.jwtParser = createJwtParser();
  }

  @Override
  public PiggyBankUser resolveToken(final String jws) {
    return parse(jws).getBody().get("user", PiggyBankUser.class);
  }

  Jws<Claims> parse(final String jws) {
    return jwtParser.parseClaimsJws(jws);
  }

  @Override
  public String create(final PiggyBankUser user) {
    return Jwts.builder()
        .signWith(key)
        .setExpiration(Date.from(instantMarker.getCurrent().plus(1, DAYS)))
        .serializeToJsonWith(JACKSON_SERIALIZER)
        .claim("user", user)
        .compact();
  }

  private JwtParser createJwtParser() {
    return Jwts.parserBuilder()
        .deserializeJsonWith(JACKSON_DESERIALIZER)
        .setSigningKey(key)
        .build();
  }
}
