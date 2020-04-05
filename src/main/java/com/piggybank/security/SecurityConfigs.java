package com.piggybank.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
class SecurityConfigs extends WebSecurityConfigurerAdapter {
  private final AuthenticationProvider authenticationProvider;

  public SecurityConfigs(AuthenticationProvider authenticationProvider) {
    this.authenticationProvider = authenticationProvider;
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
        .addFilterBefore(tokenAuthenticationFilter(), AnonymousAuthenticationFilter.class)
        .authorizeRequests()
        .anyRequest()
        .authenticated();
  }

  private TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter(authenticationProvider, SecurityContextHolder::getContext);
  }
}
