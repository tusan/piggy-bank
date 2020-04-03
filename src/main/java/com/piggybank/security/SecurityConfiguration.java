package com.piggybank.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.Filter;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  private static final RequestMatcher PUBLIC_URLS =
      new OrRequestMatcher(
          new AntPathRequestMatcher("/api/v1/users/register"),
          new AntPathRequestMatcher("/api/v1/users/login"));

  private static final RequestMatcher PROTECTED_URLS = new NegatedRequestMatcher(PUBLIC_URLS);

  private final TokenAuthenticationProvider authenticationProvider;

  public SecurityConfiguration(TokenAuthenticationProvider authenticationProvider) {
    this.authenticationProvider = authenticationProvider;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(authenticationProvider);
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().requestMatchers(PUBLIC_URLS);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement()
        .sessionCreationPolicy(STATELESS)
        .and()
        .exceptionHandling()
        .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(FORBIDDEN), PROTECTED_URLS)
        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(restAuthenticationFilter(), AnonymousAuthenticationFilter.class)
        .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .csrf()
        .disable()
        .formLogin()
        .disable()
        .httpBasic()
        .disable()
        .logout()
        .disable();
  }

  @Bean
  FilterRegistrationBean<TokenAuthenticationFilter> disableAutoRegistration() {
    FilterRegistrationBean<TokenAuthenticationFilter> registration =
        new FilterRegistrationBean<>(new TokenAuthenticationFilter(PROTECTED_URLS));
    registration.setEnabled(false);
    return registration;
  }

  private Filter restAuthenticationFilter() throws Exception {
    SimpleUrlAuthenticationSuccessHandler successHandler =
        new SimpleUrlAuthenticationSuccessHandler();
    successHandler.setRedirectStrategy(
        (request, response, url) -> {
          // DO NOTHING
        });

    TokenAuthenticationFilter filter = new TokenAuthenticationFilter(PROTECTED_URLS);

    filter.setAuthenticationManager(authenticationManager());
    filter.setAuthenticationSuccessHandler(successHandler);
    return filter;
  }
}
