package com.piggybank.security;

import com.piggybank.service.auhtentication.repository.JpaUserRepository;
import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

public interface AuthenticationResolver {
  PiggyBankUser getLoggedUser();

  final class PrincipalProvider implements AuthenticationResolver {
    private final JpaUserRepository userRepository;
    private final ISecurityContextHolder securityContextHolder;

    public PrincipalProvider(
        final JpaUserRepository userRepository,
        final ISecurityContextHolder securityContextHolder) {
      this.userRepository = userRepository;
      this.securityContextHolder = securityContextHolder;
    }

    @Override
    public PiggyBankUser getLoggedUser() {
      final Authentication authentication = securityContextHolder.getContext().getAuthentication();
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        return userRepository
            .findByUsername(authentication.getName())
            .orElseThrow(() -> new BadCredentialsException("User missing"));
      }

      throw new BadCredentialsException("Unauthorized user");
    }
  }
}
