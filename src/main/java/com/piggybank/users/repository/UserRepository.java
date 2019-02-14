package com.piggybank.users.repository;

import com.piggybank.users.dto.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> login(String username, String password);

    void register(User user);
}
