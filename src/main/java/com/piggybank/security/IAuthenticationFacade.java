package com.piggybank.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

public interface IAuthenticationFacade {
  Authentication getAuthentication();

  @Component
  final class AuthenticationFacade implements IAuthenticationFacade {
    @Override
    public Authentication getAuthentication() {
      return SecurityContextHolder.getContext().getAuthentication();
    }
  }
}
