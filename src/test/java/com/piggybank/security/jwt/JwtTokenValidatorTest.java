package com.piggybank.security.jwt;

import io.jsonwebtoken.Jwts;
import org.junit.Test;

import static com.piggybank.config.Environment.SECRET_KEY;
import static org.junit.Assert.assertEquals;

public class JwtTokenValidatorTest {
  private JwtTokenValidator sut = new JwtTokenValidator();

  @Test
  public void shouldRetrieveIssuerFromAValidToken() {
    final String jws = Jwts.builder().signWith(SECRET_KEY).setIssuer("issuer").compact();
    String actual = sut.validateAndGetIssuer(jws);

    assertEquals("issuer", actual);
  }
}
