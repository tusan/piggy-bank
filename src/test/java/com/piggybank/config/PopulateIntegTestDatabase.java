package com.piggybank.config;

import com.piggybank.service.authentication.AuthenticationService;
import com.piggybank.service.authentication.repository.PiggyBankUser;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Profile("integration")
public class PopulateIntegTestDatabase implements ApplicationListener<ApplicationReadyEvent> {
  private final AuthenticationService authenticationService;
  private static final Logger LOGGER = Logger.getLogger(PopulateLocalDatabase.class.getName());

  public PopulateIntegTestDatabase(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    LOGGER.info("START POPULATING DATABASE");

    final PiggyBankUser user = createUser(authenticationService);

    LOGGER.info("FINISHED POPULATING DATABASE");
  }

  private static PiggyBankUser createUser(final AuthenticationService authenticationService) {
    final PiggyBankUser user = new PiggyBankUser();
    user.setUsername("username");
    user.setPassword("password");

    authenticationService.add(user);
    return user;
  }
}
