package com.piggybank.security.token;

import com.piggybank.security.token.jwt.IssuerJwtTokenValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TokenValidatorFactoryTest {
  @Spy private final Key secretKey = secretKeyFor(HS256);
  @InjectMocks private TokenValidatorFactory sut;

  @Test
  public void shouldReturnIssuerJwtTokenValidator() {
    assertEquals(IssuerJwtTokenValidator.class, sut.createInstance().getClass());
  }
}
