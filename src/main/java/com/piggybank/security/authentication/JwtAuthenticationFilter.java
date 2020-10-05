package com.piggybank.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggybank.security.token.TokenAuthentication;
import com.piggybank.service.users.AuthenticationService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.piggybank.security.authentication.LoggedUserDto.forUsernameAndToken;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final ObjectMapper objectMapper;
  private final AuthenticationService authenticationService;

  public JwtAuthenticationFilter(
      final ObjectMapper objectMapper, final AuthenticationService authenticationService) {
    this.objectMapper = objectMapper;
    this.authenticationService = authenticationService;
  }

  @Override
  public Authentication attemptAuthentication(
      final HttpServletRequest request, final HttpServletResponse response)
      throws AuthenticationException {
    return readDtoFromRequest(request)
        .flatMap(r -> authenticationService.authenticate(r.username(), r.password()))
        .map(TokenAuthentication::authorizedUser)
        .orElseThrow(() -> new BadCredentialsException("Invalid User Provided"));
  }

  private Optional<LoginRequestDto> readDtoFromRequest(final ServletRequest request) {
    try {
      if (request.getContentLength() == 0) {
        return Optional.empty();
      }
      return Optional.of(objectMapper.readValue(request.getInputStream(), LoginRequestDto.class));
    } catch (final IOException e) {
      return Optional.empty();
    }
  }

  @Override
  protected void successfulAuthentication(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain chain,
      final Authentication authResult)
      throws IOException {
    final LoggedUserDto output =
        forUsernameAndToken(authResult.getName(), authResult.getCredentials().toString());

    response.getOutputStream().write(objectMapper.writeValueAsBytes(output));
  }
}
