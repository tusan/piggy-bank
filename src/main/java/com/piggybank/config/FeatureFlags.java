package com.piggybank.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureFlags {
  @Value("${piggy_bank.features.issuer_to_resolve_user}")
  private boolean useIssuerToResolveUser;

  public boolean useIssuerToResolveUser() {
    return useIssuerToResolveUser;
  }
}
