package com.piggybank.security.authentication;

import com.piggybank.config.FeatureFlags;
import com.piggybank.security.token.TokenValidator;
import com.piggybank.service.users.repository.JpaUserRepository;
import com.piggybank.service.users.repository.PiggyBankUser;
import org.junit.Before;
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
  private static final PiggyBankUser USER = piggyBankUser(TOKEN);

  @InjectMocks private JwtAuthenticationManager sut;
  @Mock private JpaUserRepository userRepository;
  @Mock private TokenValidator tokenValidator;
  @Mock private FeatureFlags featureFlags;

  @Before
  public void setUp() {
    when(userRepository.findByToken(anyString())).thenReturn(Optional.of(USER));
    when(tokenValidator.validate(anyString())).thenReturn(true);
  }

  @Test
  public void shouldReturnTheUserIfFoundByToken() {
    final Authentication actual = sut.authenticate(unauthorizedUser(TOKEN));

    assertEquals(authorizedUser(USER), actual);
  }

  @Test
  public void shouldReturnEmptyWhenTokenValidationFails() {
    when(tokenValidator.validate(anyString())).thenReturn(false);

    final Authentication actual = sut.authenticate(unauthorizedUser(TOKEN));

    assertNull(actual);
  }

  @Test
  public void shouldReturnEmptyIfNoUserFoundByToken() {
    when(userRepository.findByToken(anyString())).thenReturn(Optional.empty());

    final Authentication actual = sut.authenticate(unauthorizedUser(TOKEN));

    assertNull(actual);
  }

  @Test
  public void shouldResolveTheUserByUsernameWhen_issuer_to_resolve_user_FeatureIsEnabled() {
    when(featureFlags.useIssuerToResolveUser()).thenReturn(true);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(USER));
    when(tokenValidator.validateAndGetIssuer(anyString())).thenReturn("issuer");

    final Authentication actual = sut.authenticate(unauthorizedUser(TOKEN));

    assertEquals(authorizedUser(USER), actual);
  }

  @Test
  public void
      shouldReturnEmptyWhen_issuer_to_resolve_user_FeatureIsEnabledAndUserHasNoTokenInDatabase() {
    when(featureFlags.useIssuerToResolveUser()).thenReturn(true);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(piggyBankUser(null)));
    when(tokenValidator.validateAndGetIssuer(anyString())).thenReturn("issuer");

    final Authentication actual = sut.authenticate(unauthorizedUser(TOKEN));

    assertNull(actual);
  }

  private static PiggyBankUser piggyBankUser(final String token) {
    return forUsernamePasswordAndToken("username", "password", token);
  }
}
