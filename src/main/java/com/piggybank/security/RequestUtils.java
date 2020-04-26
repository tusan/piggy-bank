package com.piggybank.security;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class RequestUtils {
  static final String AUTHORIZATION = "Authorization";
  static final String BEARER = "Bearer";

  private RequestUtils() {}

  static Optional<String> extractBearerToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(AUTHORIZATION))
        .filter(header -> header.startsWith(BEARER))
        .map(v -> v.replace(BEARER, "").trim())
        .filter(r -> !r.isEmpty());
  }
}