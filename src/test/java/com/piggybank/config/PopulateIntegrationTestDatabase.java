package com.piggybank.config;

import com.piggybank.service.users.UserService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static com.piggybank.service.users.repository.PiggyBankUser.forUsernameAndPassword;

@Component
@Profile("integration")
public class PopulateIntegrationTestDatabase implements ApplicationListener<ApplicationReadyEvent> {
  private final UserService userService;
  private static final Logger LOGGER = Logger.getLogger(PopulateLocalDatabase.class.getName());

  public PopulateIntegrationTestDatabase(final UserService userService) {
    this.userService = userService;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    LOGGER.info("START POPULATING DATABASE");

    userService.add(forUsernameAndPassword("username", "password"));

    LOGGER.info("FINISHED POPULATING DATABASE");
  }
}
