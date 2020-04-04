package com.piggybank.security;

import org.springframework.security.core.Authentication;

public interface SecurityHolder {
  Authentication getAuthentication();
}
