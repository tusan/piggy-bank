package com.piggybank.config;

import com.piggybank.security.AuthenticationResolver;
import com.piggybank.security.TokenGenerator;
import com.piggybank.service.auhtentication.repository.JpaUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static java.lang.String.format;

@Configuration
class ApplicationContext {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  AuthenticationResolver authenticationResolver(final JpaUserRepository userRepository) {
    return username ->
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(format("Missing user %s", username)));
  }

  @Bean
  TokenGenerator tokenGenerator() {
    return () -> UUID.randomUUID().toString();
  }
}
