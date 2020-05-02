package com.piggybank.security.token;

import com.piggybank.security.token.jwt.IssuerJwtTokenBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TokenBuilderFactoryTest {

  @InjectMocks private TokenBuilderFactory sut;

  @Test
  public void shouldReturnIssuerJwtTokenValidator() {
    assertEquals(IssuerJwtTokenBuilder.class, sut.createInstance().getClass());
  }
}
