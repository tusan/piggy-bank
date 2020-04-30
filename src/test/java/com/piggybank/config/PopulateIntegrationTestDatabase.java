package com.piggybank.config;

import com.piggybank.service.users.AuthenticationService;
import com.piggybank.service.users.repository.PiggyBankUser;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static com.piggybank.service.users.repository.PiggyBankUser.forUsernameAndPassword;


@Component
@Profile("integration")
public class PopulateIntegrationTestDatabase implements ApplicationListener<ApplicationReadyEvent> {
  private final AuthenticationService authenticationService;
  private static final Logger LOGGER = Logger.getLogger(PopulateLocalDatabase.class.getName());

  public PopulateIntegrationTestDatabase(final AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    LOGGER.info("START POPULATING DATABASE");

    authenticationService.add(forUsernameAndPassword("username", "password"));

    LOGGER.info("FINISHED POPULATING DATABASE");
  }
}
