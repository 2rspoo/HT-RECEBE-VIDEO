package com.example.order.application.usecases.Order;

import com.example.order.application.ports.out.VideoRepositoryPort;
import com.example.order.domain.entities.Video;
import com.example.order.domain.entities.VideoMetadata;
import org.springframework.stereotype.Service;

@Service
public class AtualizarStatusUseCase {
    private final VideoRepositoryPort repository;

    public AtualizarStatusUseCase(VideoRepositoryPort repository) {
        this.repository = repository;

    }

    public void execute(String pedidoId, String status, String s3Url) {
        // 1. Busca o vídeo no DynamoDB
        VideoMetadata videoOriginal = (VideoMetadata) repository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Vídeo não encontrado no DynamoDB"));

        // 2. Atualiza os dados (Assumindo que VideoMetadata é um record imutável, criamos uma cópia)
        VideoMetadata videoAtualizado = new VideoMetadata(
                videoOriginal.pedidoId(),
                videoOriginal.userId(),
                videoOriginal.fileName(),
                status,          // NOVO STATUS (Ex: COMPLETED)
                s3Url,           // NOVA URL DO S3 (Caminho do ZIP)
                videoOriginal.createdAt()
        );

        // 3. Salva a versão atualizada no DynamoDB
        repository.update(videoAtualizado);
        System.out.println(">>> DynamoDB atualizado! Vídeo " + pedidoId + " agora está " + status);
    }
}
