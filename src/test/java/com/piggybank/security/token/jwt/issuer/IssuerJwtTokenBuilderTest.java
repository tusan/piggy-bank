package com.piggybank.security.token.jwt.issuer;

import com.piggybank.security.InstantMarker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;

import static com.piggybank.helpers.JwtTokenTestHelper.parseJwtToken;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IssuerJwtTokenBuilderTest {
  @InjectMocks private IssuerJwtTokenBuilder sut;

  @Mock private InstantMarker instantMarker;

  @Spy private final SecretKey SECRET_KEY = secretKeyFor(HS256);

  public static final Instant NOW = Instant.parse("3007-12-03T10:15:30.00Z");

  @Before
  public void setUp() {
    when(instantMarker.getCurrent()).thenReturn(NOW);
  }

  @Test
  public void shouldCreateAJwtTokenWithTheGivenIssuer() {
    final String jws = sut.createNew("issuer");
    String actual = parseJwtToken(jws, SECRET_KEY).getIssuer();

    assertEquals("issuer", actual);
  }

  @Test
  public void shouldSetASingleDayDurationOfTheToken() {
    final String jws = sut.createNew("issuer");

    assertEquals(Date.from(NOW.plus(1, DAYS)), parseJwtToken(jws, SECRET_KEY).getExpiration());
  }
}
