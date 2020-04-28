package com.piggybank.config;

import com.piggybank.security.SecurityContextHolderFacade;
import com.piggybank.security.InstantMarker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@Configuration
class ApplicationContext {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityContextHolderFacade securityContextHolderFacade() {
    return authentication -> SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Bean
  InstantMarker instantMarker() {
    return Instant::now;
  }
}
