package com.piggybank.security;

import com.piggybank.service.authentication.repository.JpaUserRepository;
import com.piggybank.service.authentication.repository.PiggyBankUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static com.piggybank.security.TokenAuthentication.authorizedUser;
import static com.piggybank.security.TokenAuthentication.unauthorizedUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationServiceTest {
  private static final PiggyBankUser USER = new PiggyBankUser();

  @InjectMocks private JwtAuthenticationService sut;
  @Mock private JpaUserRepository userRepository;
  @Mock private TokenValidator tokenValidator;

  @Before
  public void setUp() {
    when(userRepository.findByToken(anyString())).thenReturn(Optional.of(USER));
    when(tokenValidator.validate(anyString())).thenReturn(true);
  }

  @Test
  public void shouldReturnTheUserIfFoundByToken() {
    final Authentication actual = sut.authenticate(unauthorizedUser("token"));

    assertEquals(authorizedUser(USER), actual);
  }

  @Test
  public void shouldReturnEmptyWhenTokenValidationFails() {
    when(tokenValidator.validate(anyString())).thenReturn(false);

    final Authentication actual = sut.authenticate(unauthorizedUser("token"));

    assertNull(actual);
  }

  @Test
  public void shouldReturnEmptyIfNoUserFoundByToken() {
    when(userRepository.findByToken(anyString())).thenReturn(Optional.empty());

    final Authentication actual = sut.authenticate(unauthorizedUser("token"));

    assertNull(actual);
  }
}
