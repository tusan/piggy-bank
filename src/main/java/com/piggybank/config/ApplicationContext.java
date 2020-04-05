package com.piggybank.config;

import com.piggybank.security.AuthenticationResolver;
import com.piggybank.security.TokenGenerator;
import com.piggybank.service.auhtentication.repository.JpaUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class ApplicationContext {
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  AuthenticationResolver authenticationResolver(final JpaUserRepository userRepository) {
    return new AuthenticationResolver.PrincipalProvider(
        userRepository, SecurityContextHolder::getContext);
  }

  @Bean
  TokenGenerator tokenGenerator() {
    return () -> UUID.randomUUID().toString();
  }
}
