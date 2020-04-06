package com.piggybank.service.authentication.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JpaPiggyBankRegistrationDtoRepositoryTest {
  @Autowired private TestEntityManager testEntityManager;

  @Autowired private JpaUserRepository sut;

  @Test
  public void shouldReturnCorrectUserWhenPassingUsername() {
    final PiggyBankUser piggyBankUser1 = new PiggyBankUser();
    piggyBankUser1.setPassword("password1");
    piggyBankUser1.setToken("token1");
    piggyBankUser1.setUsername("username1");

    final PiggyBankUser piggyBankUser2 = new PiggyBankUser();
    piggyBankUser2.setPassword("password2");
    piggyBankUser2.setToken("token2");
    piggyBankUser2.setUsername("username2");

    testEntityManager.persistAndFlush(piggyBankUser1);
    testEntityManager.persistAndFlush(piggyBankUser2);

    final PiggyBankUser expected = new PiggyBankUser();
    expected.setPassword("password1");
    expected.setToken("token1");
    expected.setUsername("username1");

    Optional<PiggyBankUser> user = sut.findByUsername("username1");
    assertTrue(user.isPresent());
    assertEquals(expected, user.get());
  }
}
