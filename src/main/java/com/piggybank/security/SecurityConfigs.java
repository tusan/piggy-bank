package com.piggybank.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggybank.security.authentication.JwtAuthenticationFilter;
import com.piggybank.security.authentication.JwtAuthorizationFilter;
import com.piggybank.security.authentication.JwtLogoutHandler;
import com.piggybank.service.users.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.Filter;
import java.security.Key;
import java.security.KeyStore;

import static com.piggybank.config.Environment.JWT_KEY_ALIAS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
class SecurityConfigs extends WebSecurityConfigurerAdapter {
  public static final String LOGOUT_URL = "/api/v1/users/revoke";
  public static final String LOGIN_URL = "/api/v1/users/authorize";

  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private SecurityContextHolderFacade securityContextHolderFacade;
  @Autowired private AuthenticationService authenticationService;
  @Autowired private ObjectMapper objectMapper;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();

    http.sessionManagement()
        .sessionCreationPolicy(STATELESS)
        .and()
        .addFilter(authenticationFilter())
        .addFilterBefore(
            new JwtAuthorizationFilter(authenticationManager, securityContextHolderFacade),
            BasicAuthenticationFilter.class)
        .logout()
        .addLogoutHandler(new JwtLogoutHandler(authenticationService, objectMapper))
        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(204))
        .logoutUrl(LOGOUT_URL);

    http.authorizeRequests()
        .antMatchers(
            "/api/v1/users/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/h2-console/**")
        .permitAll()
        .anyRequest()
        .authenticated();
  }

  private Filter authenticationFilter() {
    final JwtAuthenticationFilter filter =
        new JwtAuthenticationFilter(objectMapper, authenticationService);
    filter.setFilterProcessesUrl(LOGIN_URL);
    return filter;
  }
}
