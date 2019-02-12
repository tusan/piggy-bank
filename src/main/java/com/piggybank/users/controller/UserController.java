package com.piggybank.users.controller;

import com.piggybank.users.dto.User;
import com.piggybank.users.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/users")
@CrossOrigin
class UserController {

    private UserRepository userRepository;

    @PostMapping("login")
    public ResponseEntity<Void> login(@RequestParam String username, @RequestParam String password) {
        return userRepository.login(username, password)
                .map(user -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("auth-token", user.token());
                    return headers;
                })
                .map(headers -> new ResponseEntity<Void>(headers, HttpStatus.OK))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody User user) {
        userRepository.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
