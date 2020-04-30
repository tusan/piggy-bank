package com.piggybank.api.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PiggyBankRegistrationDtoTest {
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void shouldBuildAnUserWithAllFields() throws Exception {
    String json =
        "{\n" + "    \"username\": \"username\",\n" + "    \"password\": \"password\"\n" + "}";

    final RegistrationDto expected = RegistrationDto.forUsernameAndPassword("username", "password");

    assertEquals(expected, mapper.readValue(json, RegistrationDto.class));
  }

  @Test
  public void shouldBuildAnUserWithoutToken() throws Exception {
    String json =
        "{\n" + "    \"username\": \"username\",\n" + "    \"password\": \"password\"\n" + "}";

    final RegistrationDto expected = RegistrationDto.forUsernameAndPassword("username", "password");

    assertEquals(expected, mapper.readValue(json, RegistrationDto.class));
  }
}
