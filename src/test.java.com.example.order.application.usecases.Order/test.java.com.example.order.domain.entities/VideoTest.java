package com.example.order.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VideoTest {

    @Test
    @DisplayName("Deve instanciar um Video usando o construtor padrão mantendo os valores")
    void shouldInstantiateVideoCorrectly() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String userId = "user-123";
        String fileName = "video.mp4";
        String status = "DONE";
        LocalDateTime now = LocalDateTime.now();

        // Act
        Video video = new Video(id, userId, fileName, status, now);

        // Assert
        assertEquals(id, video.id());
        assertEquals(userId, video.userId());
        assertEquals(fileName, video.fileName());
        assertEquals(status, video.status());
        assertEquals(now, video.createdAt());
    }

    @Test
    @DisplayName("Deve criar um novo Video através do factory method createNew com dados iniciais corretos")
    void shouldCreateNewVideoWithFactoryMethod() {
        // Arrange
        String userId = "user-456";
        String fileName = "meu_novo_video.mp4";

        // Act
        Video newVideo = Video.createNew(userId, fileName);

        // Assert
        // Verifica se os dados repassados estão corretos
        assertEquals(userId, newVideo.userId(), "O userId deve ser igual ao passado no método");
        assertEquals(fileName, newVideo.fileName(), "O fileName deve ser igual ao passado no método");
        
        // Verifica as regras de negócio de um novo vídeo
        assertEquals("UPLOADED", newVideo.status(), "O status inicial deve ser sempre UPLOADED");
        assertNotNull(newVideo.createdAt(), "A data de criação não pode ser nula");
        
        // Verifica se o ID foi gerado e se é um UUID válido
        assertNotNull(newVideo.id(), "O ID gerado não pode ser nulo");
        assertDoesNotThrow(() -> UUID.fromString(newVideo.id()), "O ID gerado deve ser um formato UUID válido");
    }
}