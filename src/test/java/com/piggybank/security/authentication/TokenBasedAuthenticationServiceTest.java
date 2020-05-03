package com.piggybank.security.authentication;

import com.piggybank.security.token.TokenBuilder;
import com.piggybank.service.users.repository.JpaUserRepository;
import com.piggybank.service.users.repository.PiggyBankUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.piggybank.service.users.repository.PiggyBankUser.forUsernameAndPassword;
import static com.piggybank.service.users.repository.PiggyBankUser.forUsernamePasswordAndToken;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TokenBasedAuthenticationServiceTest {
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String TOKEN = "token";

  @InjectMocks private TokenBasedAuthenticationService sut;

  @Mock private JpaUserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private TokenBuilder tokenBuilder;

  @Test
  public void shouldReturnTheAuthenticatedUserWhenSuccessfullyLogin() {
    when(tokenBuilder.createNew(anyString())).thenReturn("token_with_issuer");

    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(testUser(TOKEN)));

    Optional<PiggyBankUser> user = sut.authenticate(USERNAME, PASSWORD);

    assertTrue(user.isPresent());
    user.ifPresent(
        u -> {
          assertEquals(USERNAME, u.getUsername());
          assertEquals(PASSWORD, u.getPassword());
          assertEquals("token_with_issuer", u.getToken());
        });

    verify(userRepository).save(testUser("token_with_issuer"));
  }

  @Test
  public void shouldReturnEmptyWhenPasswordIsWrong() {
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(testUser(TOKEN)));

    Optional<PiggyBankUser> user = sut.authenticate(USERNAME, "wrong_password");

    assertFalse(user.isPresent());
  }

  @Test
  public void shouldReturnEmptyWhenUsernameIsWrong() {
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

    Optional<PiggyBankUser> user = sut.authenticate("wrong_username", PASSWORD);

    assertFalse(user.isPresent());
  }

  @Test
  public void shouldRemoveSessionFromUserWhenLoggingOut() {
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser(TOKEN)));

    final PiggyBankUser expectedPiggyBankUser = forUsernameAndPassword(USERNAME, PASSWORD);

    sut.revoke(USERNAME);

    verify(userRepository).save(expectedPiggyBankUser);
  }

  @Test
  public void shouldDoNothingWhenLoggingOutButUserIsMissing() {
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    sut.revoke(USERNAME);

    verify(userRepository).findByUsername(USERNAME);
    verify(userRepository, never()).save(any());
  }

  private PiggyBankUser testUser(final String token) {
    return forUsernamePasswordAndToken(USERNAME, PASSWORD, token);
  }
}
