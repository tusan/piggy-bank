package com.piggybank.security;

import org.junit.Test;
import org.springframework.security.authentication.RememberMeAuthenticationToken;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuthenticationByTokenProviderTest {
  private final AuthenticationByTokenProvider sut = new AuthenticationByTokenProvider();

  @Test
  public void shouldReturnTrueForExpectedType() {
    assertTrue(sut.supports(TokenAuthentication.class));
  }

  @Test
  public void shouldReturnFalseForDifferentType() {
    assertFalse(sut.supports(RememberMeAuthenticationToken.class));
  }
}
