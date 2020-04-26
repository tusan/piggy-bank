package com.piggybank.security;

import org.junit.Test;

import static com.piggybank.security.TokenValidator.DEFAULT;
import static org.junit.Assert.*;

public class TokenValidatorTest {
  @Test
  public void shouldReturnTrueForValidToken() {
    assertTrue(DEFAULT.validate("  valid_token "));
  }

  @Test
  public void shouldReturnFalseForNullToken() {
    assertFalse(DEFAULT.validate(null));
  }

  @Test
  public void shouldReturnFalseForBlankToken() {
    assertFalse(DEFAULT.validate("  "));
  }
}