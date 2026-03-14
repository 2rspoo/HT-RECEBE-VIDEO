package com.example.order.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VideoMetadataTest {

    @Test
    @DisplayName("Deve instanciar VideoMetadata corretamente e manter os valores")
    void shouldInstantiateVideoMetadataCorrectly() {
        // Arrange
        String pedidoId = "pedido-123";
        String userId = "user-456";
        String fileName = "video_original.mp4";
        String status = "PROCESSING";
        String s3Url = "s3://meu-bucket/video.zip";
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        VideoMetadata metadata = new VideoMetadata(pedidoId, userId, fileName, status, s3Url, createdAt);

        // Assert
        assertNotNull(metadata, "O objeto VideoMetadata não deveria ser nulo");
        assertEquals(pedidoId, metadata.pedidoId(), "O pedidoId deve ser igual ao passado no construtor");
        assertEquals(userId, metadata.userId(), "O userId deve ser igual ao passado no construtor");
        assertEquals(fileName, metadata.fileName(), "O fileName deve ser igual ao passado no construtor");
        assertEquals(status, metadata.status(), "O status deve ser igual ao passado no construtor");
        assertEquals(s3Url, metadata.s3Url(), "A s3Url deve ser igual à passada no construtor");
        assertEquals(createdAt, metadata.createdAt(), "A data de criação deve ser igual à passada no construtor");
    }
}