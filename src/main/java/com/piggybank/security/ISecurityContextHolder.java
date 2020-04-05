package com.piggybank.security;

import org.springframework.security.core.context.SecurityContext;

public interface ISecurityContextHolder {
  SecurityContext getContext();
}
