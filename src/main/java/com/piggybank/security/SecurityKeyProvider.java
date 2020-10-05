package com.piggybank.security;

import com.google.common.base.Preconditions;
import com.piggybank.config.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.KeyStore;
import java.util.Objects;

import static com.piggybank.config.Environment.JWT_KEY_ALIAS;

@Component("jwtKey")
public class SecurityKeyProvider extends AbstractFactoryBean<Key> {
  @Value("${security.keystore.location}")
  private Resource keystore;

  @Value("${security.keystore.password}")
  private String keystorePassword;

  @Override
  public Class<?> getObjectType() {
    return Key.class;
  }

  @Override
  protected Key createInstance() throws Exception {
    final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

    ks.load(keystore.getInputStream(), keystorePassword.toCharArray());
    return Objects.requireNonNull(ks.getKey(JWT_KEY_ALIAS, keystorePassword.toCharArray()));
  }
}
