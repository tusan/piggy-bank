package com.piggybank.users.repository;

import com.piggybank.users.dto.User;
import com.piggybank.users.repository.jpa.JpaUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public UserRepositoryImpl(JpaUserRepository userRepository, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> User.newBuilder()
                        .setId(user.getId())
                        .setPassword(user.getPassword())
                        .setUsername(user.getUsername())
                        .setToken(tokenGenerator.newToken())
                        .build());
    }

    @Override
    public void register(User user) {
        com.piggybank.users.repository.jpa.User newUser = new com.piggybank.users.repository.jpa.User();
        newUser.setUsername(user.username());
        newUser.setPassword(passwordEncoder.encode(user.password()));
        userRepository.save(newUser);
    }
}
