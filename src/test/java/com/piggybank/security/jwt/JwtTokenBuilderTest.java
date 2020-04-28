package com.piggybank.security.jwt;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Date;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JwtTokenBuilderTest {
  @InjectMocks private JwtTokenBuilder sut;

  @Mock private InstantMarker instantMarker;

  public static final Instant NOW = Instant.parse("3007-12-03T10:15:30.00Z");

  @Before
  public void setUp() {
    when(instantMarker.getCurrent()).thenReturn(NOW);
  }

  @Test
  public void shouldCreateAJwtTokenWithTheGivenIssuer() {
    final String jws = sut.createNew("issuer");
    String actual = JwtTokenToolkit.parseJwtToken(jws).getIssuer();

    assertEquals("issuer", actual);
  }

  @Test
  public void shouldSetASingleDayDurationOfTheToken() {
    final String jws = sut.createNew("issuer");

    assertEquals(Date.from(NOW.plus(1, DAYS)), JwtTokenToolkit.parseJwtToken(jws).getExpiration());
  }

}
