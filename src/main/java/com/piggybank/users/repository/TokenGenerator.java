package com.piggybank.users.repository;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenGenerator {
    public String newToken() {
        return UUID.randomUUID().toString();
    }
}
