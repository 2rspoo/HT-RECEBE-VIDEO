package com.example.order.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

// domain/entities/Video.java
public record Video(
        String id,
        String userId,
        String fileName,
        String status,
        LocalDateTime createdAt
) {
    public static Video createNew(String userId, String fileName) {
        return new Video(
                UUID.randomUUID().toString(),
                userId,
                fileName,
                "UPLOADED",
                LocalDateTime.now()
        );
    }
}