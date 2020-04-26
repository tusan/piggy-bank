package com.piggybank.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

import static com.piggybank.security.RequestUtils.extractBearerToken;

@Service
@Deprecated
public class AuthenticationByTokenService implements RememberMeServices {
  private static final Logger LOGGER =
      Logger.getLogger(AuthenticationByTokenService.class.getName());
  private final AuthenticationResolver authenticationResolver;

  AuthenticationByTokenService(final AuthenticationResolver authenticationResolver) {
    this.authenticationResolver = authenticationResolver;
  }

  @Override
  public Authentication autoLogin(
      final HttpServletRequest request, final HttpServletResponse response) {
    return extractBearerToken(request)
        .flatMap(authenticationResolver::retrieveForToken)
        .map(TokenAuthentication::authorizedUser)
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
}
