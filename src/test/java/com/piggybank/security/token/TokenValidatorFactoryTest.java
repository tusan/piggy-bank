package com.piggybank.security.token;

import com.piggybank.config.FeatureFlags;
import com.piggybank.security.token.jwt.issuer.IssuerJwtTokenValidator;
import com.piggybank.security.token.jwt.simple.SimpleJwtTokenValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.Key;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TokenValidatorFactoryTest {
  @InjectMocks private TokenValidatorFactory sut;

  @Spy private final Key secretKey = secretKeyFor(HS256);

  @Mock private FeatureFlags featureFlags;

  @Test
  public void shouldReturnSimpleJwtTokenValidator() {
    assertEquals(SimpleJwtTokenValidator.class, sut.createInstance().getClass());
  }

  @Test
  public void shouldReturnIssuerJwtTokenValidatorWhenIssuerEnabled() {
    Mockito.when(featureFlags.useIssuerToResolveUser()).thenReturn(true);
    assertEquals(IssuerJwtTokenValidator.class, sut.createInstance().getClass());
  }
}
