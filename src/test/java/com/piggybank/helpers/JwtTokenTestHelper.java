package com.piggybank.helpers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

public class JwtTokenTestHelper {

  public static Claims parseJwtToken(final String jws, final SecretKey secretKey) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(jws)
        .getBody();
  }
}
