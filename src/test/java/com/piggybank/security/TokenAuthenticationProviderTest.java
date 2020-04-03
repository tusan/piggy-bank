package com.piggybank.security;

import com.piggybank.service.auhtentication.AuthenticationService;
import com.piggybank.service.auhtentication.repository.PiggyBankUser;
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

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TokenAuthenticationProviderTest {
  @InjectMocks private TokenAuthenticationProvider sut;

  @Mock private AuthenticationService authenticationService;

  @Test
  public void shouldReturnTheAuthenticatedUserForValidToken() {
    final PiggyBankUser piggyBankUser = new PiggyBankUser();
    piggyBankUser.setUsername("username");
    piggyBankUser.setPassword("password");

    when(authenticationService.authenticateByToken(Mockito.anyString()))
        .thenReturn(Optional.of(piggyBankUser));

    final UserDetails userDetails =
        sut.retrieveUser("username", new UsernamePasswordAuthenticationToken("token", "token"));
    Assert.assertEquals("username", userDetails.getUsername());
    Assert.assertEquals("password", userDetails.getPassword());
  }

  @Test(expected = BadCredentialsException.class)
  public void shouldThrowExceptionForInvalidToken() {
    when(authenticationService.authenticateByToken(Mockito.anyString()))
        .thenReturn(Optional.empty());

    sut.retrieveUser("invalid_user", new UsernamePasswordAuthenticationToken("token", "token"));
  }
}
