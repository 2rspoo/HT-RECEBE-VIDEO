package com.example.order.infrastructure.adapters.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3StorageAdapterTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @InjectMocks
    private S3StorageAdapter adapter;

    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectCaptor;

    @Captor
    private ArgumentCaptor<RequestBody> requestBodyCaptor;

    @Captor
    private ArgumentCaptor<GetObjectPresignRequest> presignRequestCaptor;

    private final String bucketName = "meu-bucket-teste";

    @BeforeEach
    void setUp() {
        // Injeta o nome do bucket configurado via @Value
        ReflectionTestUtils.setField(adapter, "bucketName", bucketName);
    }

    @Test
    @DisplayName("Deve fazer o upload do vídeo mapeando o bucket e a key corretamente")
    void shouldUploadVideoSuccessfully() {
        // Arrange
        String videoId = "video-123";
        byte[] videoData = new byte[]{1, 2, 3, 4, 5};

        // Act
        adapter.upload(videoId, videoData);

        // Assert
        // Verifica se o cliente S3 foi chamado e captura os parâmetros
        verify(s3Client, times(1)).putObject(putObjectCaptor.capture(), requestBodyCaptor.capture());

        PutObjectRequest capturedRequest = putObjectCaptor.getValue();

        assertEquals(bucketName, capturedRequest.bucket(), "Deve usar o bucket injetado");
        assertEquals("uploads/" + videoId, capturedRequest.key(), "Deve montar a key com a pasta uploads/");

        // Garante que o corpo da requisição não é nulo (o AWS SDK gerencia a conversão de bytes internamente)
        assertNotNull(requestBodyCaptor.getValue(), "O RequestBody não pode ser nulo");
    }

    @Test
    @DisplayName("Deve gerar a URL pré-assinada de download com validade de 60 minutos")
    void shouldGenerateDownloadUrlSuccessfully() throws Exception {
        // Arrange
        String videoId = "video-123";
        String expectedUrlString = "https://meu-bucket.s3.amazonaws.com/processed/video-123.zip?X-Amz-Signature=fake";
        URL fakeUrl = new URL(expectedUrlString);

        // O SDK da AWS usa classes finais difíceis de instanciar, então nós criamos um mock do retorno
        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);
        when(mockPresignedRequest.url()).thenReturn(fakeUrl); // Ensina a devolver a nossa URL fake

        // Ensina o presigner a devolver o nosso mock quando for chamado
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(mockPresignedRequest);

        // Act
        String resultUrl = adapter.gerarUrlDownload(videoId);

        // Assert
        assertEquals(expectedUrlString, resultUrl, "A URL retornada deve ser a string extraída do presignedRequest");

        // Captura a requisição para validar como ela foi montada por dentro
        verify(s3Presigner, times(1)).presignGetObject(presignRequestCaptor.capture());
        GetObjectPresignRequest capturedPresignRequest = presignRequestCaptor.getValue();

        assertEquals(Duration.ofMinutes(60), capturedPresignRequest.signatureDuration(), "A validade deve ser de exatamente 60 minutos");
        assertEquals(bucketName, capturedPresignRequest.getObjectRequest().bucket(), "Deve apontar para o bucket correto");
        assertEquals("processed/" + videoId + ".zip", capturedPresignRequest.getObjectRequest().key(), "Deve apontar para a key correta do arquivo ZIP processado");
    }

    @Test
    @DisplayName("Deve encapsular exceções da AWS em uma RuntimeException personalizada ao gerar URL")
    void shouldThrowRuntimeExceptionWhenUrlGenerationFails() {
        // Arrange
        String videoId = "video-erro";

        // Simula a AWS lançando um erro (ex: credenciais inválidas)
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(new RuntimeException("Credenciais AWS não encontradas"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adapter.gerarUrlDownload(videoId);
        });

        // Valida se a sua mensagem customizada do try/catch foi mantida
        assertTrue(exception.getMessage().contains("Erro ao gerar a URL de download para o arquivo: " + videoId));