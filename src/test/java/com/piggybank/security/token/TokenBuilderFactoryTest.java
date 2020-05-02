package com.piggybank.security.token;

import com.piggybank.config.FeatureFlags;
import com.piggybank.security.token.jwt.issuer.IssuerJwtTokenBuilder;
import com.piggybank.security.token.jwt.simple.SimpleJwtTokenBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TokenBuilderFactoryTest {

  @InjectMocks private TokenBuilderFactory sut;

  @Mock private FeatureFlags featureFlags;

  @Test
  public void shouldReturnSimpleJwtTokenValidator() {
    assertEquals(SimpleJwtTokenBuilder.class, sut.createInstance().getClass());
  }

  @Test
  public void shouldReturnIssuerJwtTokenValidatorWhenIssuerEnabled() {
    Mockito.when(featureFlags.useIssuerToResolveUser()).thenReturn(true);
    assertEquals(IssuerJwtTokenBuilder.class, sut.createInstance().getClass());
  }
}
