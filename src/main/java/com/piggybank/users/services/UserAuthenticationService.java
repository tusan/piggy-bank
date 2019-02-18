package com.piggybank.users.services;

import com.piggybank.users.dto.User;

import java.util.Optional;

public interface UserAuthenticationService {
    Optional<User> login(String username, String password);

    Optional<User> authenticateByToken(String token);

    void logout(String username);
}