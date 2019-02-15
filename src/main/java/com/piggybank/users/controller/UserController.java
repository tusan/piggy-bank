package com.piggybank.users.controller;

import com.piggybank.users.dto.User;
import com.piggybank.users.services.UserAuthenticationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin
class UserController {

    private final UserAuthenticationService userAuthenticationService;

    public UserController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping("login")
    public ResponseEntity<Void> login(@RequestParam final String username, @RequestParam final String password) {
        return userAuthenticationService.login(username, password)
                .map(user -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "Bearer: " + user.token());
                    return headers;
                })
                .map(headers -> new ResponseEntity<Void>(headers, HttpStatus.OK))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("register")
    public ResponseEntity<Void> register(@RequestBody final User user) {
        userAuthenticationService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
