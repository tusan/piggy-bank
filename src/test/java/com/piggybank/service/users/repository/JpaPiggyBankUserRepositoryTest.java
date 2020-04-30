package com.piggybank.service.users.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.piggybank.service.users.repository.PiggyBankUser.forUsernamePasswordAndToken;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JpaPiggyBankUserRepositoryTest {
  @Autowired private TestEntityManager testEntityManager;

  @Autowired private JpaUserRepository sut;

  @Test
  public void shouldReturnCorrectUserWhenPassingUsername() {
    final PiggyBankUser piggyBankUser1 =
        forUsernamePasswordAndToken("username1", "password1", "token1");
    final PiggyBankUser piggyBankUser2 =
        forUsernamePasswordAndToken("username2", "password2", "token2");

    testEntityManager.persistAndFlush(piggyBankUser1);
    testEntityManager.persistAndFlush(piggyBankUser2);

    Optional<PiggyBankUser> user = sut.findByUsername("username1");
    assertTrue(user.isPresent());
    assertEquals(piggyBankUser1, user.get());
  }
}
