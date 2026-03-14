package com.example.order.domain.entities;

import java.time.LocalDateTime;

public record VideoMetadata(
        String pedidoId,
        String userId,
        String fileName,
        String status,
        String s3Url,
        LocalDateTime createdAt
) {}
