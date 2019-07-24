package com.piggybank.users.services;

import com.piggybank.model.JpaUserRepository;
import com.piggybank.users.dto.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TokenBasedAuthenticationServiceTest {
  @InjectMocks private TokenBasedAuthenticationService sut;

  @Mock private JpaUserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private TokenGenerator tokenGenerator;

  @Before
  public void setUp() {
    Mockito.when(tokenGenerator.newToken()).thenReturn("token");
  }

  @Test
  public void shouldReturnTheAuthenticatedUserWhenSuccessfullyLogin() {
    Mockito.when(
            passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
        .thenReturn(true);
    Mockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
        .thenReturn(Optional.of(testUser()));

    Optional<User> user = sut.login("username", "password");

    assertTrue(user.isPresent());

    assertEquals(
        User.newBuilder().setUsername("username").setPassword("password").setToken("token").build(),
        user.get());

    Mockito.verify(userRepository).save(testUser());
  }

  @Test
  public void shouldReturnEmptyWhenPasswordIsWrong() {
    Mockito.when(
            passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
        .thenReturn(false);
    Mockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
        .thenReturn(Optional.of(testUser()));

    Optional<User> user = sut.login("username", "wrong_password");

    assertFalse(user.isPresent());
  }

  @Test
  public void shouldReturnEmptyWhenUsernameIsWrong() {
    Mockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class)))
        .thenReturn(Optional.empty());

    Optional<User> user = sut.login("wrong_username", "password");

    assertFalse(user.isPresent());
  }

  @Test
  public void shouldReturnTheUserIfFoundByToken() {
    Mockito.when(userRepository.findByToken(ArgumentMatchers.anyString()))
        .thenReturn(Optional.of(testUser()));

    Optional<User> user = sut.authenticateByToken("token");

    assertTrue(user.isPresent());

    assertEquals(
        User.newBuilder().setUsername("username").setPassword("password").setToken("token").build(),
        user.get());
  }

  @Test
  public void shouldReturnEmptyIfNoUserFoundByToken() {
    Mockito.when(userRepository.findByToken(ArgumentMatchers.anyString()))
        .thenReturn(Optional.empty());

    Optional<User> user = sut.authenticateByToken("token");

    assertFalse(user.isPresent());
  }

  @Test
  public void shouldRemoveSessionFromUserWhenLoggingOut() {
    Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
        .thenReturn(Optional.of(testUser()));

    com.piggybank.model.User expectedUser = new com.piggybank.model.User();
    expectedUser.setUsername("username");
    expectedUser.setPassword("password");

    sut.logout("username");

    Mockito.verify(userRepository).save(expectedUser);
  }

  @Test
  public void shouldDoNothingWhenLoggingOutButUserIsMissing() {
    Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
        .thenReturn(Optional.empty());

    sut.logout("username");

    Mockito.verify(userRepository).findByUsername("username");
    Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any());
  }

  private com.piggybank.model.User testUser() {
    com.piggybank.model.User expectedUser = new com.piggybank.model.User();
    expectedUser.setUsername("username");
    expectedUser.setPassword("password");
    expectedUser.setToken("token");
    return expectedUser;
  }
}
