package com.piggybank.service.users;

import com.piggybank.service.users.repository.JpaUserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.piggybank.service.users.repository.PiggyBankUser.forUsernameAndPassword;
import static com.piggybank.service.users.repository.PiggyBankUser.forUsernamePasswordAndToken;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JpaUserServiceTest {
  @InjectMocks private JpaUserService sut;

  @Mock private JpaUserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Test
  public void shouldEncodePasswordAndSaveNewUser() {
    when(passwordEncoder.encode("password")).thenReturn("encoded-password");
    sut.add(forUsernameAndPassword("password", "token"));
    verify(userRepository)
        .save(forUsernamePasswordAndToken("username", "encoded-password", "token"));
  }
}
