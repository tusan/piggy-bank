package com.piggybank.api.users;

import com.piggybank.service.users.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.piggybank.service.users.repository.PiggyBankUser.forUsernameAndPassword;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin
class UsersController {

  private final AuthenticationService authenticationService;

  public UsersController(final AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping("register")
  public ResponseEntity<Void> register(@RequestBody final RegistrationDto registrationDto) {
    authenticationService.add(
        forUsernameAndPassword(
            registrationDto.username(), registrationDto.password()));

    return ResponseEntity.status(CREATED).build();
  }
}
