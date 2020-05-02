package com.piggybank.security.token;

import com.piggybank.security.InstantMarker;
import com.piggybank.security.token.jwt.IssuerJwtTokenBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@DependsOn("jwtKey")
final class TokenBuilderFactory extends AbstractFactoryBean<TokenBuilder> {
  @Autowired private InstantMarker instantMarker;

  @Autowired
  @Qualifier("jwtKey")
  private Key securityKey;

  @Override
  public Class<?> getObjectType() {
    return TokenBuilder.class;
  }

  @Override
  protected TokenBuilder createInstance() {
    return new IssuerJwtTokenBuilder(instantMarker, securityKey);
  }
}
