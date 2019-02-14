package com.piggybank.users.services;

import com.piggybank.users.dto.User;
import com.piggybank.users.repository.TokenGenerator;
import com.piggybank.users.repository.jpa.JpaUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TokenBasedAuthenticationService implements UserAuthenticationService {
    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public TokenBasedAuthenticationService(JpaUserRepository userRepository, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    user.setToken(tokenGenerator.newToken());
                    userRepository.save(user);
                    return user;
                })
                .map(this::convertEntityToDto);
    }

    private User convertEntityToDto(com.piggybank.users.repository.jpa.User user) {
        return User.newBuilder()
                .setPassword(user.getPassword())
                .setUsername(user.getUsername())
                .setToken(user.getToken())
                .build();
    }

    @Override
    public Optional<User> authenticateByToken(String token) {
        return userRepository.findByToken(token)
                .map(this::convertEntityToDto);
    }

    @Override
    public void logout(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    user.setToken(null);
                    userRepository.save(user);
                });
    }

    @Override
    public void register(User user) {
        com.piggybank.users.repository.jpa.User newUser = new com.piggybank.users.repository.jpa.User();
        newUser.setUsername(user.username());
        newUser.setPassword(passwordEncoder.encode(user.password()));
        userRepository.save(newUser);
    }
}
