package com.piggybank.security;

import com.piggybank.security.filters.JwtAuthorizationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
class SecurityConfigs extends WebSecurityConfigurerAdapter {
  private final AuthenticationManager authenticationManager;
  private final SecurityContextHolderFacade securityContextHolderFacade;

  public SecurityConfigs(
      final AuthenticationManager authenticationManager,
      final SecurityContextHolderFacade securityContextHolderFacade) {
    this.authenticationManager = authenticationManager;
    this.securityContextHolderFacade = securityContextHolderFacade;
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
        .antMatchers(
            "/api/v1/users/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/h2-console/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();

    http.sessionManagement()
        .sessionCreationPolicy(STATELESS)
        .and()
        .addFilter(new JwtAuthorizationFilter(authenticationManager, securityContextHolderFacade))
        .authorizeRequests()
        .anyRequest()
        .authenticated();
  }
}
