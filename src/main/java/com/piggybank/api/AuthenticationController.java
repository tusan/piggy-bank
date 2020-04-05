package com.piggybank.api;

import com.piggybank.service.auhtentication.AuthenticationService;
import com.piggybank.service.auhtentication.dto.LoggedUserDto;
import com.piggybank.service.auhtentication.dto.LoginRequestDto;
import com.piggybank.service.auhtentication.dto.LogoutDto;
import com.piggybank.service.auhtentication.dto.RegistrationDto;
import com.piggybank.service.auhtentication.repository.JpaUserRepository;
import com.piggybank.service.auhtentication.repository.PiggyBankUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.piggybank.service.auhtentication.dto.LoggedUserDto.forUsernameAndToken;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin
class AuthenticationController {

  private final AuthenticationService authenticationService;
  private final JpaUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthenticationController(
      final AuthenticationService authenticationService,
      final JpaUserRepository userRepository,
      final PasswordEncoder passwordEncoder) {
    this.authenticationService = authenticationService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
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
    PiggyBankUser newPiggyBankUser = new PiggyBankUser();
    newPiggyBankUser.setUsername(registrationDto.username());
    newPiggyBankUser.setPassword(passwordEncoder.encode(registrationDto.password()));
    userRepository.save(newPiggyBankUser);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("revoke")
  public ResponseEntity<Void> revoke(@RequestBody final LogoutDto logoutDto) {
    authenticationService.revoke(logoutDto.username());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
