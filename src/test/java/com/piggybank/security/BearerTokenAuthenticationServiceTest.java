package com.piggybank.security;

import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.piggybank.security.BearerTokenAuthenticationService.AUTHORIZATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BearerTokenAuthenticationServiceTest {
  @InjectMocks private BearerTokenAuthenticationService sut;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private AuthenticationResolver authenticationResolver;

  @Test
  public void shouldAuthenticateUserIfValidTokenIsInRequestHeader() {
    when(authenticationResolver.retrieveForToken("a token")).thenReturn(Optional.of(testUser()));
    when(request.getHeader(AUTHORIZATION)).thenReturn("a token");

    final Authentication actual = sut.autoLogin(request, response);
    final BearerTokenAuthentication expected =
        BearerTokenAuthentication.authorizedUser(testUser());

    assertEquals(expected, actual);
  }

  public void shouldReturnNullIfTokenHeaderIsMissing() {
    when(request.getHeader(AUTHORIZATION)).thenReturn(null);

    assertNull(sut.autoLogin(request, response));
    verifyNoInteractions(authenticationResolver);
  }

  private PiggyBankUser testUser() {
    PiggyBankUser expectedPiggyBankUser = new PiggyBankUser();
    expectedPiggyBankUser.setUsername("username");
    expectedPiggyBankUser.setPassword("password");
    expectedPiggyBankUser.setToken("a token");
    return expectedPiggyBankUser;
  }
}
