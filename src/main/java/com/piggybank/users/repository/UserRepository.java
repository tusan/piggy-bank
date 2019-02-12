package com.piggybank.users.repository;

import com.piggybank.users.dto.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> login(String username, String password);

    Optional<User> findByToken(String token);

    void register(User user);
}
