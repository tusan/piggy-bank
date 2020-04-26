package com.piggybank.security;

import org.springframework.security.core.Authentication;

public interface SecurityContextHolderFacade {
  void setAuthentication(Authentication authentication);
}
