package com.piggybank.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureFlags {
  @Value("${piggy_bank.features.issuer_to_resolve_user}")
  private boolean useIssuerToResolveUser;

  @Value("${piggy_bank.features.jwt_token_type}")
  private boolean useJwtToken;

  public boolean useIssuerToResolveUser() {
    return useIssuerToResolveUser;
  }

  public boolean useJwtToken() {
    return useJwtToken;
  }
}
