package com.piggybank.security;

import com.piggybank.service.auhtentication.repository.JpaUserRepository;
import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

public interface AuthenticationResolver {
  PiggyBankUser getLoggedUser();

  @Component
  final class PrincipalProvider implements AuthenticationResolver {
    private final JpaUserRepository userRepository;
    private final IAuthenticationFacade authenticationFacade;

    PrincipalProvider(
        final JpaUserRepository userRepository, final IAuthenticationFacade authenticationFacade) {
      this.userRepository = userRepository;
      this.authenticationFacade = authenticationFacade;
    }

    @Override
    public PiggyBankUser getLoggedUser() {
      final Authentication authentication = authenticationFacade.getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        return userRepository
            .findByUsername(authentication.getName())
            .orElseThrow(() -> new BadCredentialsException("User missing"));
      }

      throw new BadCredentialsException("Unauthorized user");
    }
  }
}
