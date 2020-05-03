package com.piggybank.helpers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.security.Key;

public class JwtTokenTestHelper {

  public static Claims parseJwtToken(final String jws, final Key secretKey) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(jws)
        .getBody();
  }
}
