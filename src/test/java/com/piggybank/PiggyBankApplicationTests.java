package com.piggybank;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings("EmptyMethod")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
public class PiggyBankApplicationTests {

    @Test
    public void contextLoads() {
    }

}
