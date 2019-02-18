package com.piggybank.users.services;

import com.piggybank.model.JpaUserRepository;
import com.piggybank.users.dto.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TokenBasedAuthenticationService implements UserAuthenticationService {
    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public TokenBasedAuthenticationService(final JpaUserRepository userRepository,
                                           final PasswordEncoder passwordEncoder,
                                           final TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Optional<User> login(final String username, final String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    user.setToken(tokenGenerator.newToken());
                    userRepository.save(user);
                    return user;
                })
                .map(this::convertEntityToDto);
    }

    @Override
    public Optional<User> authenticateByToken(final String token) {
        return userRepository.findByToken(token)
                .map(this::convertEntityToDto);
    }

    @Override
    public void logout(final String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    user.setToken(null);
                    userRepository.save(user);
                });
    }

    @Override
    public void register(final User user) {
        com.piggybank.model.User newUser = new com.piggybank.model.User();
        newUser.setUsername(user.username());
        newUser.setPassword(passwordEncoder.encode(user.password()));
        userRepository.save(newUser);
    }

    private User convertEntityToDto(final com.piggybank.model.User user) {
        return User.newBuilder()
                .setPassword(user.getPassword())
                .setUsername(user.getUsername())
                .setToken(user.getToken())
                .build();
    }
}
