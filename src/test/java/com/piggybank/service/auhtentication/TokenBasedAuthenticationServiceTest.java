package com.piggybank.service.auhtentication;

import com.piggybank.security.TokenGenerator;
import com.piggybank.service.auhtentication.repository.JpaUserRepository;
import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TokenBasedAuthenticationServiceTest {
  @InjectMocks private TokenBasedAuthenticationService sut;

  @Mock private JpaUserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private TokenGenerator tokenGenerator;

  @Before
  public void setUp() {
    when(tokenGenerator.newToken()).thenReturn("token");
  }

  @Test
  public void shouldReturnTheAuthenticatedUserWhenSuccessfullyLogin() {
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(testUser()));

    Optional<PiggyBankUser> user = sut.authenticate("username", "password");

    assertTrue(user.isPresent());
    user.ifPresent(
        u -> {
          assertEquals("username", u.getUsername());
          assertEquals("password", u.getPassword());
          assertEquals("token", u.getToken());
        });

    verify(userRepository).save(testUser());
  }

  @Test
  public void shouldReturnEmptyWhenPasswordIsWrong() {
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(testUser()));

    Optional<PiggyBankUser> user = sut.authenticate("username", "wrong_password");

    assertFalse(user.isPresent());
  }

  @Test
  public void shouldReturnEmptyWhenUsernameIsWrong() {
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

    Optional<PiggyBankUser> user = sut.authenticate("wrong_username", "password");

    assertFalse(user.isPresent());
  }

  @Test
  public void shouldReturnTheUserIfFoundByToken() {
    when(userRepository.findByToken(anyString())).thenReturn(Optional.of(testUser()));

    Optional<PiggyBankUser> user = sut.retrieveForToken("token");

    assertTrue(user.isPresent());
    user.ifPresent(
        u -> {
          assertEquals("username", u.getUsername());
          assertEquals("password", u.getPassword());
          assertEquals("token", u.getToken());
        });
  }

  @Test
  public void shouldReturnEmptyIfNoUserFoundByToken() {
    when(userRepository.findByToken(anyString())).thenReturn(Optional.empty());

    Optional<PiggyBankUser> user = sut.retrieveForToken("token");

    assertFalse(user.isPresent());
  }

  @Test
  public void shouldRemoveSessionFromUserWhenLoggingOut() {
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser()));

    PiggyBankUser expectedPiggyBankUser = new PiggyBankUser();
    expectedPiggyBankUser.setUsername("username");
    expectedPiggyBankUser.setPassword("password");

    sut.revoke("username");

    verify(userRepository).save(expectedPiggyBankUser);
  }

  @Test
  public void shouldDoNothingWhenLoggingOutButUserIsMissing() {
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    sut.revoke("username");

    verify(userRepository).findByUsername("username");
    verify(userRepository, never()).save(any());
  }

  @Test
  public void shouldEncodePasswordAndSaveNewUser() {
    when(passwordEncoder.encode("password")).thenReturn("encoded-password");
    sut.add(testUser());
    verify(userRepository).save(testUser("encoded-password"));
  }

  private PiggyBankUser testUser() {
    return testUser("password");
  }

  private PiggyBankUser testUser(final String password) {
    PiggyBankUser expectedPiggyBankUser = new PiggyBankUser();
    expectedPiggyBankUser.setUsername("username");
    expectedPiggyBankUser.setPassword(password);
    expectedPiggyBankUser.setToken("token");
    return expectedPiggyBankUser;
  }
}
