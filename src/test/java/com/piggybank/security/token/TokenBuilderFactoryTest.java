package com.piggybank.security.token;

import com.piggybank.security.token.jwt.IssuerJwtTokenBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TokenBuilderFactoryTest {

  @InjectMocks private TokenBuilderFactory sut;

  @Test
  public void shouldReturnIssuerJwtTokenValidator() {
    assertEquals(IssuerJwtTokenBuilder.class, sut.createInstance().getClass());
  }
}
