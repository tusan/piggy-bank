package com.piggybank.users.repository.jpa;

import com.piggybank.users.dto.User;
import com.piggybank.users.repository.TokenGenerator;
import com.piggybank.users.repository.UserRepositoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryImplTest {
    @InjectMocks
    private UserRepositoryImpl sut;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGenerator tokenGenerator;

    @Before
    public void setUp() {
        Mockito.when(tokenGenerator.newToken()).thenReturn("token");
    }

    @Test
    public void shouldReturnTheAuthenticatedUserWhenSuccessfullyLogin() {
        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class))).thenReturn(Optional.of(testUser()));

        Optional<User> user = sut.login("username", "password");

        assertTrue(user.isPresent());

        assertEquals(User.newBuilder()
                .setUsername("username")
                .setPassword("password")
                .setToken("token")
                .setId(1L)
                .build(), user.get());
    }

    @Test
    public void shouldReturnEmptyWhenPasswordIsWrong() {
        Mockito.when(passwordEncoder.matches(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class))).thenReturn(Optional.of(testUser()));

        Optional<User> user = sut.login("username", "wrong_password");

        assertFalse(user.isPresent());
    }

    @Test
    public void shouldReturnEmptyWhenUsernameIsWrong() {
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.any(String.class))).thenReturn(Optional.empty());

        Optional<User> user = sut.login("wrong_username", "password");

        assertFalse(user.isPresent());
    }

    @Test
    public void shouldSaveTheGivenUserInRepository() {
        Mockito.when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn("encoded_password");

        com.piggybank.users.repository.jpa.User expectedUser = new com.piggybank.users.repository.jpa.User();
        expectedUser.setUsername("username");
        expectedUser.setPassword("encoded_password");

        User user = User.newBuilder()
                .setUsername("username")
                .setPassword("password")
                .build();

        sut.register(user);

        Mockito.verify(userRepository).save(expectedUser);
    }

    private com.piggybank.users.repository.jpa.User testUser() {
        com.piggybank.users.repository.jpa.User expectedUser = new com.piggybank.users.repository.jpa.User();
        expectedUser.setId(1L);
        expectedUser.setUsername("username");
        expectedUser.setPassword("password");
        expectedUser.setToken("token");
        return expectedUser;
    }
}