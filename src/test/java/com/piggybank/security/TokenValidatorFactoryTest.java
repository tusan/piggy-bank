package com.piggybank.security;

import com.piggybank.config.FeatureFlags;
import com.piggybank.security.jwt.IssuerJwtTokenValidator;
import com.piggybank.security.jwt.SimpleJwtTokenValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TokenValidatorFactoryTest {
  @InjectMocks private TokenValidatorFactory sut;

  @Mock private FeatureFlags featureFlags;

  @Test
  public void shouldReturnUuidTokenValidatorWhenAllFlagsAreOff() {
    assertEquals(TokenValidator.DEFAULT, sut.createInstance());
  }

  @Test
  public void shouldReturnSimpleJwtTokenValidatorWhenJwtEnabledAndIssuerDisabled() {
    Mockito.when(featureFlags.useJwtToken()).thenReturn(true);
    assertEquals(SimpleJwtTokenValidator.class, sut.createInstance().getClass());
  }

  @Test
  public void shouldReturnIssuerJwtTokenValidatorWhenJwtEnabledAndIssuerEnabled() {
    Mockito.when(featureFlags.useJwtToken()).thenReturn(true);
    Mockito.when(featureFlags.useIssuerToResolveUser()).thenReturn(true);
    assertEquals(IssuerJwtTokenValidator.class, sut.createInstance().getClass());
  }
}
