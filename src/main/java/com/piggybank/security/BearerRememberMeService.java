package com.piggybank.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class BearerRememberMeService implements RememberMeServices, AuthenticationProvider {
  private static final Logger LOGGER = Logger.getLogger(BearerRememberMeService.class.getName());

  static final String AUTHORIZATION = "Authorization";
  static final String BEARER = "Bearer";

  private final AuthenticationResolver authenticationResolver;

  BearerRememberMeService(final AuthenticationResolver authenticationResolver) {
    this.authenticationResolver = authenticationResolver;
  }

  @Override
  public Authentication autoLogin(
      final HttpServletRequest request, final HttpServletResponse response) {
    return Optional.ofNullable(request.getHeader(AUTHORIZATION))
        .map(v -> v.replace(BEARER, "").trim())
        .flatMap(authenticationResolver::retrieveForToken)
        .map(ValidAuthenticationToken::authorizedUser)
        .orElse(null);
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

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    return authentication;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return ValidAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
