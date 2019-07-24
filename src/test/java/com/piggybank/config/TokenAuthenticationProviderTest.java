package com.piggybank.config;

import com.piggybank.users.dto.User;
import com.piggybank.users.services.UserAuthenticationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TokenAuthenticationProviderTest {
  @InjectMocks private TokenAuthenticationProvider sut;

  @Mock private UserAuthenticationService userAuthenticationService;

  @Test
  public void shouldReturnTheAuthenticatedUserForValidToken() {
    Mockito.when(userAuthenticationService.authenticateByToken(Mockito.anyString()))
        .thenReturn(
            Optional.of(User.newBuilder().setUsername("username").setPassword("password").build()));

    final UserDetails user =
        sut.retrieveUser("username", new UsernamePasswordAuthenticationToken("token", "token"));
    Assert.assertEquals("username", user.getUsername());
    Assert.assertEquals("password", user.getPassword());
  }

  @Test(expected = BadCredentialsException.class)
  public void shouldThrowExceptionForInvalidToken() {
    Mockito.when(userAuthenticationService.authenticateByToken(Mockito.anyString()))
        .thenReturn(Optional.empty());

    sut.retrieveUser("invalid_user", new UsernamePasswordAuthenticationToken("token", "token"));
  }
}
