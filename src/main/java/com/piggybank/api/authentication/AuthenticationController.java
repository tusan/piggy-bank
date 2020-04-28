package com.piggybank.api.authentication;

import com.piggybank.api.authentication.dto.LoggedUserDto;
import com.piggybank.api.authentication.dto.LoginRequestDto;
import com.piggybank.api.authentication.dto.LogoutDto;
import com.piggybank.api.authentication.dto.RegistrationDto;
import com.piggybank.service.authentication.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import static com.piggybank.api.authentication.dto.LoggedUserDto.forUsernameAndToken;
import static com.piggybank.service.authentication.repository.PiggyBankUser.forUsernameAndPassword;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin
class AuthenticationController {

  private final AuthenticationService authenticationService;

  public AuthenticationController(final AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping("authorize")
  public ResponseEntity<LoggedUserDto> authorize(
      @RequestBody final LoginRequestDto loginRequestDto) {
    return authenticationService
        .authenticate(loginRequestDto.username(), loginRequestDto.password())
        .map(user -> forUsernameAndToken(user.getUsername(), user.getToken()))
        .map(ResponseEntity::ok)
        .orElseThrow(
            () ->
                new UsernameNotFoundException(
                    String.format("User not found [request=%s]", loginRequestDto)));
  }

  @PostMapping("register")
  public ResponseEntity<Void> register(@RequestBody final RegistrationDto registrationDto) {
    authenticationService.add(
        forUsernameAndPassword(registrationDto.username(), registrationDto.password()));

    return ResponseEntity.status(CREATED).build();
  }

  @PostMapping("revoke")
  public ResponseEntity<Void> revoke(@RequestBody final LogoutDto logoutDto) {
    authenticationService.revoke(logoutDto.username());
    return ResponseEntity.status(NO_CONTENT).build();
  }
}
