package com.example.order.application.usecases.Order;

import com.example.order.application.ports.out.VideoQueuePort;
import com.example.order.application.ports.out.VideoRepositoryPort;
import com.example.order.application.ports.out.VideoStoragePort;
import com.example.order.domain.entities.Video;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadVideoUseCaseTest {

    @Mock
    private VideoRepositoryPort repository;

    @Mock
    private VideoStoragePort storage;

    @Mock
    private VideoQueuePort queue;

    @InjectMocks
    private UploadVideoUseCase useCase;

    @Test
    @DisplayName("Deve criar o vídeo, salvar no banco, fazer upload e enviar para a fila")
    void shouldExecuteUploadFlowSuccessfully() {
        // 1. Preparação (Arrange)
        String userId = "user-123";
        String fileName = "meu-video.mp4";
        byte[] videoData = new byte[]{10, 20, 30}; // Bytes falsos do vídeo

        // Nota: Como os métodos das portas provavelmente retornam void (save, upload, sendToProcess),
        // não precisamos configurar 'when(...).thenReturn(...)'. O Mockito já entende isso.

        // 2. Execução (Act)
        String generatedVideoId = useCase.execute(userId, fileName, videoData);

        // 3. Verificações (Assert)
        assertNotNull(generatedVideoId, "O ID do vídeo gerado não pode ser nulo");

        // Verifica se salvou a entidade no banco
        verify(repository, times(1)).save(any(Video.class));

        // Verifica se fez o upload no S3 passando EXATAMENTE o id gerado e os bytes corretos
        verify(storage, times(1)).upload(eq(generatedVideoId), eq(videoData));

        // Verifica se enviou o evento para a fila SQS
        verify(queue, times(1)).sendToProcess(any(Video.class));
    }

    @Test
    @DisplayName("Deve interromper o fluxo e lançar exceção se o upload falhar")
    void shouldStopFlowIfUploadFails() {
        // 1. Preparação
        String userId = "user-123";
        String fileName = "meu-video.mp4";
        byte[] videoData = new byte[]{10, 20, 30};

        // Força o S3 a dar erro na hora do upload
        doThrow(new RuntimeException("S3 fora do ar!"))
                .when(storage).upload(anyString(), any(byte[].class));

        // 2. Execução e Verificação
        // Como o storage falhou, o execute deve repassar essa exceção para cima
        assertThrows(RuntimeException.class, () -> {
            useCase.execute(userId, fileName, videoData);
        });

        // O repository.save é chamado ANTES do storage.upload no seu código,
        // então ele DEVE ter sido executado 1 vez.
        verify(repository, times(1)).save(any(Video.class));

        // Como o upload estourou erro, a notificação para a fila NUNCA deve ser chamada
        verify(queue, never()).sendToProcess(any(Video.class));
    }
}