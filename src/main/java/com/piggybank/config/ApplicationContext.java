package com.piggybank.config;

import com.piggybank.security.TokenBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
class ApplicationContext {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  TokenBuilder tokenGenerator() {
    return () -> UUID.randomUUID().toString();
  }
}
