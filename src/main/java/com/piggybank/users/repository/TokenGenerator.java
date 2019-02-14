package com.piggybank.users.repository;

import java.util.UUID;

public class TokenGenerator {
    public String newToken() {
        return UUID.randomUUID().toString();
    }
}
