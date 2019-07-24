package com.piggybank.users.services;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class TokenGenerator {
  public String newToken() {
    return UUID.randomUUID().toString();
  }
}
