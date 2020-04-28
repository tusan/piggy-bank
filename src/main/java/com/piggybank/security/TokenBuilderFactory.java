package com.piggybank.security;

import com.piggybank.config.FeatureFlags;
import com.piggybank.security.jwt.InstantMarker;
import com.piggybank.security.jwt.IssuerJwtTokenBuilder;
import com.piggybank.security.jwt.SimpleJwtTokenBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

@Component
final class TokenBuilderFactory extends AbstractFactoryBean<TokenBuilder> {
  @Autowired private FeatureFlags featureFlags;
  @Autowired private InstantMarker instantMarker;

  @Override
  public Class<?> getObjectType() {
    return TokenBuilder.class;
  }

  @Override
  protected TokenBuilder createInstance() {
    if(featureFlags.useJwtToken() && featureFlags.useIssuerToResolveUser()) {
      return new IssuerJwtTokenBuilder(instantMarker);
    }

    if(featureFlags.useJwtToken()) {
      return new SimpleJwtTokenBuilder(instantMarker);
    }

    return TokenBuilder.DEFAULT;
  }
}
