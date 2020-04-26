package com.piggybank.config;

import com.piggybank.security.SecurityContextHolderFacade;
import com.piggybank.security.TokenBuilder;
import com.piggybank.security.TokenValidator;
import com.piggybank.security.jwt.JwtTokenHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;

@Configuration
class ApplicationContext {
  private static final Logger LOGGER = Logger.getLogger(ApplicationContext.class.getName());

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityContextHolderFacade securityContextHolderFacade() {
    return authentication -> SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Bean
  @ConditionalOnProperty(
      name = "authentication.token.type",
      havingValue = "uuid",
      matchIfMissing = true)
  TokenValidator noOpTokenValidator() {
    return TokenValidator.DEFAULT;
  }

  @Bean
  @ConditionalOnProperty(
      name = "authentication.token.type",
      havingValue = "uuid",
      matchIfMissing = true)
  TokenBuilder uuidTokenGenerator() {
    return () -> UUID.randomUUID().toString();
  }

  @Bean
  @ConditionalOnProperty(name = "authentication.token.type", havingValue = "jwt")
  JwtTokenHelper jwtTokenHelper() {
    LOGGER.info("Using jwtTokenGenerator");
    return new JwtTokenHelper(Instant::now);
  }
}
