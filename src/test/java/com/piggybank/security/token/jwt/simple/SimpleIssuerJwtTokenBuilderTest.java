package com.piggybank.security.token.jwt.simple;

import com.piggybank.config.Environment;
import com.piggybank.security.InstantMarker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.crypto.SecretKey;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;

import static com.piggybank.helpers.JwtTokenTestHelper.parseJwtToken;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimpleIssuerJwtTokenBuilderTest {
  public static final SecretKey SECRET_KEY = secretKeyFor(HS256);
  @InjectMocks private SimpleJwtTokenBuilder sut;

  @Spy private Key secretKey = SECRET_KEY;

  @Mock private InstantMarker instantMarker;

  private static final Instant NOW = Instant.parse("3007-12-03T10:15:30.00Z");

  @Before
  public void setUp() {
    when(instantMarker.getCurrent()).thenReturn(NOW);
  }

  @Test
  public void shouldCreateAndResolveJwtTokenWithTheDefaultKey() {
    final String jws = sut.createNew();
    String actual = parseJwtToken(jws, SECRET_KEY).getIssuer();

    assertEquals(Environment.ISSUER, actual);
  }

  @Test
  public void shouldSetASingleDayDurationOfTheToken() {
    final String jws = sut.createNew();

    assertEquals(Date.from(NOW.plus(1, DAYS)), parseJwtToken(jws, SECRET_KEY).getExpiration());
  }
}
