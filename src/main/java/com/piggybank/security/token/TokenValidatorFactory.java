package com.piggybank.security.token;

import com.piggybank.config.FeatureFlags;
import com.piggybank.security.token.jwt.issuer.IssuerJwtTokenValidator;
import com.piggybank.security.token.jwt.simple.SimpleJwtTokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

@Component
final class TokenValidatorFactory extends AbstractFactoryBean<TokenValidator> {
  @Autowired private FeatureFlags featureFlags;

  @Override
  public Class<?> getObjectType() {
    return TokenValidator.class;
  }

  @Override
  protected TokenValidator createInstance() {
    if (featureFlags.useJwtToken() && featureFlags.useIssuerToResolveUser()) {
      return new IssuerJwtTokenValidator();
    }

    if (featureFlags.useJwtToken()) {
      return new SimpleJwtTokenValidator();
    }

    return TokenValidator.DEFAULT;
  }
}
