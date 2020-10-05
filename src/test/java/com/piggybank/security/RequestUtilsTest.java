package com.piggybank.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.piggybank.security.RequestUtils.AUTHORIZATION;
import static com.piggybank.security.RequestUtils.extractBearerToken;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RequestUtilsTest {
  @Mock private HttpServletRequest request;

  @Test
  public void shouldReturnTheBearerTokenIfPresentInTheRequest() {
    Mockito.when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer token_123");
    final Optional<String> result = extractBearerToken(request);

    assertTrue(result.isPresent());
    result.ifPresent(r -> assertEquals("token_123", r));
  }

  @Test
  public void shouldReturnEmptyIfHeaderIsNotPresent() {
    Mockito.when(request.getHeader(AUTHORIZATION)).thenReturn(null);

    assertFalse(extractBearerToken(request).isPresent());
  }

  @Test
  public void shouldReturnEmptyIfHeaderIsBlank() {
    Mockito.when(request.getHeader(AUTHORIZATION)).thenReturn("  ");
    assertFalse(extractBearerToken(request).isPresent());
  }

  @Test
  public void shouldReturnEmptyIfHeaderIsNotInBearerFormat() {
    Mockito.when(request.getHeader(AUTHORIZATION)).thenReturn("token_123");

    assertFalse(extractBearerToken(request).isPresent());
  }
}
