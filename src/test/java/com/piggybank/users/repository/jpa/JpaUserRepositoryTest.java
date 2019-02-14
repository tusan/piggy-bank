package com.piggybank.users.repository.jpa;

import org.junit.Before;
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
public class JpaUserRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaUserRepository sut;

    @Before
    public void setUp() {
        final User user1 = new User();
        user1.setPassword("password1");
        user1.setToken("token1");
        user1.setUsername("username1");

        final User user2 = new User();
        user2.setPassword("password2");
        user2.setToken("token2");
        user2.setUsername("username2");

        testEntityManager.persistAndFlush(user1);
        testEntityManager.persistAndFlush(user2);
    }

    @Test
    public void shouldReturnCorrectUserWhenPassingUsername() {
        final User expected = new User();
        expected.setPassword("password1");
        expected.setToken("token1");
        expected.setUsername("username1");

        Optional<User> user = sut.findByUsername("username1");
        assertTrue(user.isPresent());
        assertEquals(expected, user.get());
    }
}