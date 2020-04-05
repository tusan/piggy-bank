package com.piggybank.api;

import com.piggybank.service.auhtentication.AuthenticationService;
import com.piggybank.service.auhtentication.dto.LoggedUserDto;
import com.piggybank.service.auhtentication.dto.LoginRequestDto;
import com.piggybank.service.auhtentication.dto.LogoutDto;
import com.piggybank.service.auhtentication.dto.RegistrationDto;
import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import static com.piggybank.service.auhtentication.dto.LoggedUserDto.forUsernameAndToken;
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
        .orElseThrow(() -> new BadCredentialsException("wrong credential for user"));
  }

  @PostMapping("register")
  public ResponseEntity<Void> register(@RequestBody final RegistrationDto registrationDto) {
    final PiggyBankUser newPiggyBankUser = new PiggyBankUser();
    newPiggyBankUser.setUsername(registrationDto.username());
    newPiggyBankUser.setPassword(registrationDto.password());

    authenticationService.add(newPiggyBankUser);

    return ResponseEntity.status(CREATED).build();
  }

  @PostMapping("revoke")
  public ResponseEntity<Void> revoke(@RequestBody final LogoutDto logoutDto) {
    authenticationService.revoke(logoutDto.username());
    return ResponseEntity.status(NO_CONTENT).build();
  }
}
