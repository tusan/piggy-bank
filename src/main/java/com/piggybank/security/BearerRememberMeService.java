package com.piggybank.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.logging.Logger;

class BearerRememberMeService implements RememberMeServices {
  static final String AUTHORIZATION = "Authorization";
  static final String BEARER = "Bearer";

  private static final Logger LOGGER = Logger.getLogger(BearerRememberMeService.class.getName());

  private final AuthenticationProvider authenticationProvider;

  BearerRememberMeService(AuthenticationProvider authenticationProvider) {
    this.authenticationProvider = authenticationProvider;
  }

  @Override
  public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
    return resolveAuthenticationToken(request)
        .map(UnauthorizedAuthenticationToken::unauthorizedFromToken)
        .map(authenticationProvider::authenticate)
        .orElse(null);
  }

  private Optional<String> resolveAuthenticationToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(AUTHORIZATION))
        .map(v -> v.replace(BEARER, "").trim());
  }

  @Override
  public void loginFail(HttpServletRequest request, HttpServletResponse response) {
    LOGGER.info("Failed to auto-login user");
  }

  @Override
  public void loginSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication successfulAuthentication) {
    LOGGER.info(String.format("User authenticated. [user=%s]", successfulAuthentication));
  }
}
