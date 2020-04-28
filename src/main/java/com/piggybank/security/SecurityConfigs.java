package com.piggybank.security;

import com.piggybank.security.filters.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.security.Key;
import java.security.KeyStore;

import static com.piggybank.config.Environment.JWT_KEY_ALIAS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
class SecurityConfigs extends WebSecurityConfigurerAdapter {
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private SecurityContextHolderFacade securityContextHolderFacade;

  @Value("${security.keystore.location}")
  private Resource keystore;

  @Value("${security.keystore.password}")
  private String keystorePassword;

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

  @Bean(name = "jwtKey")
  public Key jwtKey() throws Exception {
    final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    ks.load(keystore.getInputStream(), keystorePassword.toCharArray());

    return ks.getKey(JWT_KEY_ALIAS, keystorePassword.toCharArray());
  }
}
