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
        // Injeta o nome do bucket falso para o teste não quebrar
        ReflectionTestUtils.setField(adapter, "bucketName", bucketName);
    }

    @Test
    @DisplayName("Deve fazer o upload do vídeo mapeando o bucket e a key corretamente")
    void shouldUploadVideoSuccessfully() {
        // Arrange
        String videoId = "video-123";
        byte[] videoData = new byte[]{1, 2, 3};

        // Act
        adapter.upload(videoId, videoData);

        // Assert
        verify(s3Client, times(1)).putObject(putObjectCaptor.capture(), requestBodyCaptor.capture());

        PutObjectRequest capturedRequest = putObjectCaptor.getValue();
        assertEquals(bucketName, capturedRequest.bucket(), "Deve usar o bucket injetado");
        assertEquals("uploads/" + videoId, capturedRequest.key(), "Deve montar a key com a pasta uploads/");
        assertNotNull(requestBodyCaptor.getValue(), "O RequestBody não pode ser nulo");
    }

    @Test
    @DisplayName("Deve gerar a URL pré-assinada de download com validade de 60 minutos")
    void shouldGenerateDownloadUrlSuccessfully() throws Exception {
        // Arrange
        String videoId = "video-123";
        String expectedUrlString = "https://meu-bucket.s3.amazonaws.com/processed/video-123.zip";
        URL fakeUrl = new URL(expectedUrlString);

        PresignedGetObjectRequest mockPresignedRequest = mock(PresignedGetObjectRequest.class);
        when(mockPresignedRequest.url()).thenReturn(fakeUrl);

        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(mockPresignedRequest);

        // Act
        String resultUrl = adapter.gerarUrlDownload(videoId);

        // Assert
        assertEquals(expectedUrlString, resultUrl, "A URL retornada deve ser a extraída do mock");

        verify(s3Presigner, times(1)).presignGetObject(presignRequestCaptor.capture());
        GetObjectPresignRequest capturedPresignRequest = presignRequestCaptor.getValue();

        assertEquals(Duration.ofMinutes(60), capturedPresignRequest.signatureDuration(), "Validade de 60 min");
        assertEquals(bucketName, capturedPresignRequest.getObjectRequest().bucket(), "Bucket correto");
        assertEquals("processed/" + videoId + ".zip", capturedPresignRequest.getObjectRequest().key(), "Key correta");
    }

    @Test
    @DisplayName("Deve encapsular exceções da AWS em uma RuntimeException personalizada")
    void shouldThrowExceptionWhenUrlGenerationFails() {
        // Arrange
        String videoId = "video-erro";

        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(new RuntimeException("Erro simulado da AWS"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adapter.gerarUrlDownload(videoId);
        });

        assertTrue(exception.getMessage().contains("Erro ao gerar a URL de download para o arquivo: " + videoId));
    }
}