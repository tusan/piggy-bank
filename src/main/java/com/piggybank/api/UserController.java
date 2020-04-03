package com.piggybank.api;

import com.piggybank.service.auhtentication.AuthenticationService;
import com.piggybank.service.auhtentication.dto.LoggedUserDto;
import com.piggybank.service.auhtentication.dto.LoginRequestDto;
import com.piggybank.service.auhtentication.dto.UserDto;
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
class UserController {

  private final AuthenticationService authenticationService;
  private final JpaUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserController(
      final AuthenticationService authenticationService,
      final JpaUserRepository userRepository,
      final PasswordEncoder passwordEncoder) {
    this.authenticationService = authenticationService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("login")
  public ResponseEntity<LoggedUserDto> login(@RequestBody final LoginRequestDto loginRequestDto) {
    return authenticationService
        .login(loginRequestDto.username(), loginRequestDto.password())
        .map(user -> forUsernameAndToken(user.getUsername(), user.getToken()))
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new BadCredentialsException("wrong credential for user"));
  }

  @PostMapping("register")
  public ResponseEntity<Void> register(@RequestBody final UserDto userDto) {
    PiggyBankUser newPiggyBankUser = new PiggyBankUser();
    newPiggyBankUser.setUsername(userDto.username());
    newPiggyBankUser.setPassword(passwordEncoder.encode(userDto.password()));
    userRepository.save(newPiggyBankUser);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
