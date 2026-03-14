package com.example.order.infrastructure.adapters.sqs;

import com.example.order.domain.entities.Video;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqsQueueAdapterTest {

    @Mock
    private SqsTemplate sqsTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SqsQueueAdapter adapter;

    private final String queueUrl = "https://sqs.sa-east-1.amazonaws.com/123456789/fila-processamento-video";

    private Video video;

    @BeforeEach
    void setUp() {
        // Injeta a URL configurada no properties para o teste
        ReflectionTestUtils.setField(adapter, "queueUrl", queueUrl);

        video = new Video(
                "video-123",
                "user-456",
                "meu_video.mp4",
                "UPLOADED",
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve converter o vídeo para JSON puro e enviar para o SQS com sucesso")
    void shouldSerializeAndSendToSqsSuccessfully() throws Exception {
        // Arrange
        String jsonEsperado = "{\"id\":\"video-123\",\"fileName\":\"meu_video.mp4\"}";

        // Ensina o ObjectMapper mockado a retornar a string JSON quando receber o objeto Video
        when(objectMapper.writeValueAsString(video)).thenReturn(jsonEsperado);

        // Act
        adapter.sendToProcess(video);

        // Assert
        // Verifica se o ObjectMapper foi chamado garantindo o "pulo do gato"
        verify(objectMapper, times(1)).writeValueAsString(video);

        // Verifica se o SQS enviou a mensagem para a URL correta usando o JSON puro
        verify(sqsTemplate, times(1)).send(queueUrl, jsonEsperado);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException caso o ObjectMapper falhe ao gerar o JSON")
    void shouldThrowExceptionWhenSerializationFails() throws Exception {
        // Arrange
        // Simula uma falha interna do Jackson ao tentar converter o objeto
        when(objectMapper.writeValueAsString(any(Video.class)))
                .thenThrow(new JsonProcessingException("Erro de parsing fake") {});

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adapter.sendToProcess(video);
        });

        // Valida se o erro foi encapsulado corretamente com a mensagem esperada
        assertEquals("Erro ao serializar mensagem SQS para envio", exception.getMessage());

        // Garante que o template do SQS NUNCA foi chamado já que a conversão falhou antes
        verify(sqsTemplate, never()).send(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar RuntimeException caso a AWS/SQSTemplate falhe no envio")
    void shouldThrowExceptionWhenSqsFails() throws Exception {
        // Arrange
        String jsonEsperado = "{\"id\":\"video-123\"}";
        when(objectMapper.writeValueAsString(video)).thenReturn(jsonEsperado);

        // Simula uma falha de rede ou de permissão na hora de enviar para a fila
        doThrow(new RuntimeException("Credenciais SQS inválidas"))
                .when(sqsTemplate).send(anyString(), anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adapter.sendToProcess(video);
        });

        assertEquals("Erro ao serializar mensagem SQS para envio", exception.getMessage());
    }
}