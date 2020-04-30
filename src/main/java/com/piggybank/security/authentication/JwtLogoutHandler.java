package com.piggybank.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggybank.service.users.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JwtLogoutHandler extends SecurityContextLogoutHandler {
  private final AuthenticationService authenticationService;
  private final ObjectMapper objectMapper;

  public JwtLogoutHandler(
      final AuthenticationService authenticationService, final ObjectMapper objectMapper) {
    this.authenticationService = authenticationService;
    this.objectMapper = objectMapper;
  }

  @Override
  public void logout(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final Authentication authentication) {
    super.logout(request, response, authentication);
    getUsername(request).map(LogoutDto::username).ifPresent(authenticationService::revoke);
  }

  private Optional<LogoutDto> getUsername(HttpServletRequest request) {
    try {
      return Optional.of(objectMapper.readValue(request.getInputStream(), LogoutDto.class));
    } catch (IOException e) {
      return Optional.empty();
    }
  }
}
