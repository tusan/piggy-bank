package com.piggybank.security.jwt;

import io.jsonwebtoken.Jwts;
import org.junit.Test;

import static com.piggybank.config.Environment.ISSUER;
import static com.piggybank.config.Environment.SECRET_KEY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleIssuerJwtTokenValidatorTest {
  private final SimpleJwtTokenValidator sut =
      new SimpleJwtTokenValidator();

  @Test
  public void shouldReturnTrueForTheProperIssuer() {
    final String jws = Jwts.builder().signWith(SECRET_KEY).setIssuer(ISSUER).compact();
    assertTrue(sut.validate(jws));
  }

  @Test
  public void shouldReturnFalseForDifferentIssuer() {
    final String jws =
        Jwts.builder().signWith(SECRET_KEY).setIssuer("Not a valid issuer").compact();
    assertFalse(sut.validate(jws));
  }
}
