package com.piggybank.users.controller;

import com.piggybank.model.JpaUserRepository;
import com.piggybank.users.dto.LoggedUser;
import com.piggybank.users.dto.LoginRequest;
import com.piggybank.users.dto.User;
import com.piggybank.users.services.UserAuthenticationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin
class UserController {

    private final UserAuthenticationService userAuthenticationService;
    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(final UserAuthenticationService userAuthenticationService,
                          final JpaUserRepository userRepository,
                          final PasswordEncoder passwordEncoder) {
        this.userAuthenticationService = userAuthenticationService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("login")
    public ResponseEntity<LoggedUser> login(@RequestBody final LoginRequest loginRequest) {
        return userAuthenticationService.login(loginRequest.username(), loginRequest.password())
                .map(user -> LoggedUser.forUsernameAndToken(user.username(), user.token()))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BadCredentialsException("wrong credential for user"));
    }

    @PostMapping("register")
    public ResponseEntity<Void> register(@RequestBody final User user) {
        com.piggybank.model.User newUser = new com.piggybank.model.User();
        newUser.setUsername(user.username());
        newUser.setPassword(passwordEncoder.encode(user.password()));
        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
