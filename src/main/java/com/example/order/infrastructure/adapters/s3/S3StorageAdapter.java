package com.example.order.infrastructure.adapters.s3;

import com.example.order.application.ports.out.VideoStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

// infrastructure/adapters/s3/S3StorageAdapter.java
@Component
public class S3StorageAdapter implements VideoStoragePort {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner; // Adicionado para gerar os links

    @Value("${aws.s3.bucket}")
    private String bucketName;

    // Construtor atualizado com as duas dependências
    public S3StorageAdapter(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public void upload(String videoId, byte[] data) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key("uploads/" + videoId)
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(data));
    }

    // Novo método que gera o link apontando para o arquivo ZIP
    public String gerarUrlDownload(String videoId) {
        try {
            // 1. Monta a key exatamente onde a App 2 salvou o ZIP
            String s3Key = "processed/" + videoId + ".zip";

            // 2. Informa qual bucket e arquivo queremos pegar
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            // 3. Define o tempo de validade do link gerado (Ex: 60 minutos)
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(60))
                    .getObjectRequest(getObjectRequest)
                    .build();

            // 4. Assina a URL
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toString();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar a URL de download para o arquivo: " + videoId, e);
        }
    }
}