package com.piggybank.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.piggybank.security.RequestUtils.AUTHORIZATION;
import static com.piggybank.security.RequestUtils.extractBearerToken;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
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
