package com.example.demo.model;

import java.time.Instant;

public record DemoMessage(String key, String message, Instant createdAt) {
    public static DemoMessage of(String key, String message) {
        return new DemoMessage(key, message, Instant.now());
    }
}
