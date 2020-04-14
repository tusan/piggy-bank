package com.piggybank.security;

import org.junit.Test;
import org.springframework.security.authentication.RememberMeAuthenticationToken;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TokenAuthenticationProviderTest {
  private final BearerTokenAuthenticationProvider sut = new BearerTokenAuthenticationProvider();

  @Test
  public void shouldReturnTrueForExpectedType() {
    assertTrue(sut.supports(TokenAuthentication.class));
  }

  @Test
  public void shouldReturnFalseForDifferentType() {
    assertFalse(sut.supports(RememberMeAuthenticationToken.class));
  }
}
