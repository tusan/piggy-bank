package com.piggybank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
public class PiggyBankApplicationTests {

  @SuppressWarnings("EmptyMethod")
  @Test
  public void contextLoads() {}
}
