package com.piggybank.security.authentication;

import com.piggybank.security.token.TokenValidator;
import com.piggybank.service.users.repository.JpaUserRepository;
import com.piggybank.service.users.repository.PiggyBankUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static com.piggybank.security.token.TokenAuthentication.authorizedUser;
import static com.piggybank.security.token.TokenAuthentication.unauthorizedUser;
import static com.piggybank.service.users.repository.PiggyBankUser.forUsernamePasswordAndToken;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationManagerTest {
  public static final String TOKEN = "token";
  private static final PiggyBankUser USER =
      forUsernamePasswordAndToken("username", "password", TOKEN);

  @InjectMocks private JwtAuthenticationManager sut;
  @Mock private JpaUserRepository userRepository;
  @Mock private TokenValidator tokenValidator;

  @Test
  public void shouldResolveTheUserByUsername() {
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(USER));
    when(tokenValidator.validateAndGetIssuer(anyString())).thenReturn("issuer");

    final Authentication actual = sut.authenticate(unauthorizedUser(TOKEN));

    assertEquals(authorizedUser(USER), actual);
  }

  @Test
  public void shouldReturnEmptyWhenTokenValidationFails() {
    when(tokenValidator.validateAndGetIssuer(anyString())).thenReturn(null);

    final Authentication actual = sut.authenticate(unauthorizedUser(TOKEN));

    assertNull(actual);
  }

  @Test
  public void shouldReturnEmptyIfNoUserFound() {
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
    when(tokenValidator.validateAndGetIssuer(anyString())).thenReturn("issuer");

    final Authentication actual = sut.authenticate(unauthorizedUser(TOKEN));

    assertNull(actual);
  }
}
