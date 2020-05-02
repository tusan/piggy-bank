package com.piggybank.security.token;

import com.piggybank.security.token.jwt.IssuerJwtTokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@DependsOn("jwtKey")
final class TokenValidatorFactory extends AbstractFactoryBean<TokenValidator> {
  @Autowired
  @Qualifier("jwtKey")
  private Key securityKey;

  @Override
  public Class<?> getObjectType() {
    return TokenValidator.class;
  }

  @Override
  protected TokenValidator createInstance() {
    return new IssuerJwtTokenValidator(securityKey);
  }
}
