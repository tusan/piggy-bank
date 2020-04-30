package com.piggybank.security.token.jwt.simple;

import io.jsonwebtoken.Jwts;
import org.junit.Test;

import java.security.Key;

import static com.piggybank.config.Environment.ISSUER;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleIssuerJwtTokenValidatorTest {
  private static final Key SECURITY_KEY = secretKeyFor(HS256);

  private final SimpleJwtTokenValidator sut = new SimpleJwtTokenValidator(SECURITY_KEY);

  @Test
  public void shouldReturnTrueForTheProperIssuer() {
    final String jws = Jwts.builder().signWith(SECURITY_KEY).setIssuer(ISSUER).compact();
    assertTrue(sut.validate(jws));
  }

  @Test
  public void shouldReturnFalseForDifferentIssuer() {
    final String jws =
        Jwts.builder().signWith(SECURITY_KEY).setIssuer("Not a valid issuer").compact();
    assertFalse(sut.validate(jws));
  }
}
