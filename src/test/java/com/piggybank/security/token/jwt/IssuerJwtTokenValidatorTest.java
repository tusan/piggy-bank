package com.piggybank.security.token.jwt;

import io.jsonwebtoken.Jwts;
import org.junit.Test;

import java.security.Key;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static org.junit.Assert.assertEquals;

public class IssuerJwtTokenValidatorTest {
  private final static Key SECURITY_KEY = secretKeyFor(HS256);
  private final IssuerJwtTokenValidator sut = new IssuerJwtTokenValidator(SECURITY_KEY);

  @Test
  public void shouldRetrieveIssuerFromAValidToken() {
    final String jws = Jwts.builder().signWith(SECURITY_KEY).setIssuer("issuer").compact();
    String actual = sut.validateAndGetIssuer(jws);

    assertEquals("issuer", actual);
  }
}
