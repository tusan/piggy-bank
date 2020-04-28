package com.piggybank.security;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public final class RequestUtils {
  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer";

  private RequestUtils() {}

  public static Optional<String> extractBearerToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(AUTHORIZATION))
        .filter(header -> header.startsWith(BEARER))
        .map(v -> v.replace(BEARER, "").trim())
        .filter(r -> !r.isEmpty());
  }
}
