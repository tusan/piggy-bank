package com.piggybank.security.token.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import static com.piggybank.config.Environment.SECRET_KEY;

public class JwtTokenToolkit {
  public static Claims parseJwtToken(String jws) {
    return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(jws).getBody();
  }
}
