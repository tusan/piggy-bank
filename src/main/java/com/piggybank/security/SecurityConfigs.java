package com.piggybank.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
class SecurityConfigs extends WebSecurityConfigurerAdapter {
  private final BearerRememberMeService bearerRememberMeService;

  public SecurityConfigs(final BearerRememberMeService bearerRememberMeService) {
    this.bearerRememberMeService = bearerRememberMeService;
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
        .antMatchers("/api/v1/users/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();

    http.sessionManagement()
        .sessionCreationPolicy(STATELESS)
        .and()
        .rememberMe()
        .rememberMeServices(bearerRememberMeService)
        .and()
        .authorizeRequests()
        .anyRequest()
        .authenticated();
  }
}
