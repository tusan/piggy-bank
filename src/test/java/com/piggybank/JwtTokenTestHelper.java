package com.piggybank;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.Key;

public final class JwtTokenTestHelper {

  public static Claims parseJwtToken(final String jws, final Key secretKey) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(jws)
        .getBody();
  }
}
