package com.piggybank.users.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldBuildAnUserWithAllFields() throws Exception {
        String json = "{\n" +
                "    \"id\": 1,\n" +
                "    \"username\": \"username\",\n" +
                "    \"password\": \"password\",\n" +
                "    \"token\": \"token\"\n" +
                "}";

        User expected = User.newBuilder()
                .setId(1L)
                .setUsername("username")
                .setPassword("password")
                .setToken("token")
                .build();

        assertEquals(expected, mapper.readValue(json, User.class));
    }

    @Test
    public void shouldBuildAnUserWithoutToken() throws Exception {
        String json = "{\n" +
                "    \"id\": 1,\n" +
                "    \"username\": \"username\",\n" +
                "    \"password\": \"password\"\n" +
                "}";

        User expected = User.newBuilder()
                .setId(1L)
                .setUsername("username")
                .setPassword("password")
                .build();

        assertEquals(expected, mapper.readValue(json, User.class));
    }

    @Test
    public void shouldBuildAnUserWithoutId() throws Exception {
        String json = "{\n" +
                "    \"username\": \"username\",\n" +
                "    \"password\": \"password\"\n" +
                "}";

        User expected = User.newBuilder()
                .setUsername("username")
                .setPassword("password")
                .build();

        assertEquals(expected, mapper.readValue(json, User.class));
    }

}