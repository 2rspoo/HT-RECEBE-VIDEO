package com.example.order.application.usecases.Order;

import com.example.order.application.ports.out.VideoRepositoryPort;
import com.example.order.domain.entities.VideoMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarStatusUseCaseTest {

    @Mock
    private VideoRepositoryPort repository;

    @InjectMocks
    private AtualizarStatusUseCase useCase;

    @Captor
    private ArgumentCaptor<VideoMetadata> videoCaptor;

    private VideoMetadata videoOriginal;

    @BeforeEach
    void setUp() {
        videoOriginal = new VideoMetadata(
                "pedido-123",
                "user-789",
                "meu_video.mp4",
                "PROCESSING",
                "s3://raw/meu_video.mp4",
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve buscar, atualizar os dados e salvar no repositório com sucesso")
    void shouldUpdateStatusAndUrlSuccessfully() {
        // Arrange
        String pedidoId = "pedido-123";
        String novoStatus = "COMPLETED";
        String novaUrlS3 = "s3://processados/pedido-123.zip";

        // Ensina o mock a retornar o vídeo original quando procurarem pelo ID
        when(repository.findById(pedidoId)).thenReturn(Optional.of(videoOriginal));

        // Act
        useCase.execute(pedidoId, novoStatus, novaUrlS3);

        // Assert
        // Verifica se o update foi chamado e captura o objeto que foi passado para ele
        verify(repository, times(1)).update(videoCaptor.capture());

        VideoMetadata videoAtualizado = videoCaptor.getValue();

        // Valida se os campos que DEVEM mudar realmente mudaram
        assertEquals(novoStatus, videoAtualizado.status(), "O status deve ter sido atualizado");
        assertEquals(novaUrlS3, videoAtualizado.s3Url(), "A URL do S3 deve ter sido atualizada");

        // Valida se os campos que NÃO DEVEM mudar continuam iguais
        assertEquals(videoOriginal.pedidoId(), videoAtualizado.pedidoId(), "O ID do pedido não deve mudar");
        assertEquals(videoOriginal.userId(), videoAtualizado.userId(), "O ID do usuário não deve mudar");
        assertEquals(videoOriginal.fileName(), videoAtualizado.fileName(), "O nome do arquivo não deve mudar");
        assertEquals(videoOriginal.createdAt(), videoAtualizado.createdAt(), "A data de criação não deve mudar");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o vídeo não for encontrado no DynamoDB")
    void shouldThrowExceptionWhenVideoNotFound() {
        // Arrange
        String pedidoId = "pedido-inexistente";

        // Simula o banco retornando vazio
        when(repository.findById(pedidoId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            useCase.execute(pedidoId, "COMPLETED", "s3://fake-url.zip");
        });

        // Verifica se a mensagem de erro é exatamente a que você definiu no código
        assertEquals("Vídeo não encontrado no DynamoDB", exception.getMessage());

        // Garante que o update NUNCA foi chamado, já que o processo abortou antes
        verify(repository, never()).update(any(VideoMetadata.class));
    }
}