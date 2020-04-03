package com.piggybank.service.auhtentication.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PiggyBankUserDtoTest {
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void shouldBuildAnUserWithAllFields() throws Exception {
    String json =
        "{\n"
            + "    \"id\": 1,\n"
            + "    \"username\": \"username\",\n"
            + "    \"password\": \"password\",\n"
            + "    \"token\": \"token\"\n"
            + "}";

    UserDto expected =
        UserDto.newBuilder()
            .setId(1L)
            .setUsername("username")
            .setPassword("password")
            .setToken("token")
            .build();

    assertEquals(expected, mapper.readValue(json, UserDto.class));
  }

  @Test
  public void shouldBuildAnUserWithoutToken() throws Exception {
    String json =
        "{\n"
            + "    \"id\": 1,\n"
            + "    \"username\": \"username\",\n"
            + "    \"password\": \"password\"\n"
            + "}";

    UserDto expected =
        UserDto.newBuilder().setId(1L).setUsername("username").setPassword("password").build();

    assertEquals(expected, mapper.readValue(json, UserDto.class));
  }

  @Test
  public void shouldBuildAnUserWithoutId() throws Exception {
    String json =
        "{\n" + "    \"username\": \"username\",\n" + "    \"password\": \"password\"\n" + "}";

    UserDto expected = UserDto.newBuilder().setUsername("username").setPassword("password").build();

    assertEquals(expected, mapper.readValue(json, UserDto.class));
  }
}
