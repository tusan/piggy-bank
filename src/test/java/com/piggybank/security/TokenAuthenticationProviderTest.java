package com.piggybank.security;

import com.piggybank.service.auhtentication.AuthenticationService;
import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.piggybank.security.ValidAuthenticationToken.unauthorizedFromToken;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TokenAuthenticationProviderTest {
  @InjectMocks private TokenAuthenticationProvider sut;

  @Mock private AuthenticationService authenticationService;

  @Test
  public void shouldReturnTheAuthenticatedUserForValidToken() {
    final PiggyBankUser piggyBankUser = new PiggyBankUser();
    piggyBankUser.setUsername("username");
    piggyBankUser.setToken("token");

    when(authenticationService.retrieveForToken(anyString()))
        .thenReturn(Optional.of(piggyBankUser));

    final Authentication authentication = sut.authenticate(unauthorizedFromToken("token"));

    System.out.println(authentication);

    assertTrue(authentication.isAuthenticated());
    assertEquals("token", authentication.getCredentials());
    assertEquals("username", authentication.getPrincipal());
  }

  @Test(expected = UsernameNotFoundException.class)
  public void shouldThrowExceptionForInvalidToken() {
    when(authenticationService.retrieveForToken(anyString())).thenReturn(Optional.empty());
    sut.authenticate(unauthorizedFromToken("token"));
  }
}
