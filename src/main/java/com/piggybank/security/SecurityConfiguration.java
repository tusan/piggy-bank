package com.piggybank.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  private static final RequestMatcher PUBLIC_URLS =
      new OrRequestMatcher(
          new AntPathRequestMatcher("/api/v1/users/register"),
          new AntPathRequestMatcher("/api/v1/users/authorize"),
          new AntPathRequestMatcher("/api/v1/users/revoke"));

  private final AuthenticationProvider authenticationProvider;

  public SecurityConfiguration(AuthenticationProvider authenticationProvider) {
    this.authenticationProvider = authenticationProvider;
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
        .requestMatchers(PUBLIC_URLS)
        .antMatchers(
            "/v3/api-docs/**",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**");
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
