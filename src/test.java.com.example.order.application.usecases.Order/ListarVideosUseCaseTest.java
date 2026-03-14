package com.example.order.application.usecases.Order;

import com.example.order.application.ports.out.VideoRepositoryPort;
import com.example.order.domain.entities.VideoMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarVideosUseCaseTest {

    @Mock
    private VideoRepositoryPort videoRepositoryPort;

    @InjectMocks
    private ListarVideosUseCase useCase;

    @Test
    @DisplayName("Deve retornar a lista de vídeos de um usuário com sucesso")
    void shouldReturnVideoListSuccessfully() {
        // Arrange
        String userId = "user-123";

        VideoMetadata video1 = new VideoMetadata(
                "pedido-1", userId, "video1.mp4", "DONE", "s3://url1", LocalDateTime.now()
        );
        VideoMetadata video2 = new VideoMetadata(
                "pedido-2", userId, "video2.mp4", "PROCESSING", null, LocalDateTime.now()
        );

        List<VideoMetadata> expectedVideos = List.of(video1, video2);

        // Ensina o mock a retornar a lista falsa quando procurarem pelo userId
        when(videoRepositoryPort.findByUserId(userId)).thenReturn(expectedVideos);

        // Act
        List<VideoMetadata> actualVideos = useCase.execute(userId);

        // Assert
        assertNotNull(actualVideos, "A lista retornada não deveria ser nula");
        assertEquals(2, actualVideos.size(), "A lista deveria conter exatamente 2 vídeos");
        assertEquals(expectedVideos, actualVideos, "A lista retornada deve ser idêntica à do repositório");

        // Garante que o repositório foi chamado passando o ID correto
        verify(videoRepositoryPort, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando o usuário não tiver vídeos")
    void shouldReturnEmptyListWhenUserHasNoVideos() {
        // Arrange
        String userId = "user-sem-videos";

        // Simula o repositório retornando uma lista vazia
        when(videoRepositoryPort.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<VideoMetadata> actualVideos = useCase.execute(userId);

        // Assert
        assertNotNull(actualVideos, "A lista não deveria ser nula, mesmo sem vídeos");
        assertTrue(actualVideos.isEmpty(), "A lista deveria estar vazia");

        verify(videoRepositoryPort, times(1)).findByUserId(userId);
    }
}