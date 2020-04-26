package com.piggybank.security;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RequestUtilsTest {
  private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
  @Test
  public void shouldReturnTheBearerTokenIfPresentInTheRequest() {
    Mockito.when(request.getHeader(RequestUtils.AUTHORIZATION)).thenReturn("Bearer token_123");
    Optional<String> result = RequestUtils.extractBearerToken(request);

    Assert.assertTrue(result.isPresent());
    result.ifPresent(r -> assertEquals("token_123", r));
  }

  @Test
  public void shouldReturnEmptyIfHeaderIsNotPresent() {
    Mockito.when(request.getHeader(RequestUtils.AUTHORIZATION)).thenReturn(null);
    Optional<String> result = RequestUtils.extractBearerToken(request);

    assertFalse(result.isPresent());
  }

  @Test
  public void shouldReturnEmptyIfHeaderIsBlank() {
    Mockito.when(request.getHeader(RequestUtils.AUTHORIZATION)).thenReturn("  ");
    Optional<String> result = RequestUtils.extractBearerToken(request);

    assertFalse(result.isPresent());
  }

  @Test
  public void shouldReturnEmptyIfHeaderIsNotInBearerFormat() {
    Mockito.when(request.getHeader(RequestUtils.AUTHORIZATION)).thenReturn("token_123");
    Optional<String> result = RequestUtils.extractBearerToken(request);

    assertFalse(result.isPresent());
  }
}
