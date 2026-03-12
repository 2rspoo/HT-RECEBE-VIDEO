package com.example.order.infrastructure.Controllers;

import com.example.order.application.ports.out.VideoRepositoryPort;
import com.example.order.domain.entities.VideoMetadata;
import com.example.order.infrastructure.adapters.s3.S3StorageAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos")
public class VideoDownloadController {

    private final VideoRepositoryPort videoRepositoryPort;
    private final S3StorageAdapter s3StorageAdapter;

    // Injetando o RepositoryPort direto no Controller para buscar o status
    public VideoDownloadController(VideoRepositoryPort videoRepositoryPort, S3StorageAdapter s3StorageAdapter) {
        this.videoRepositoryPort = videoRepositoryPort;
        this.s3StorageAdapter = s3StorageAdapter;
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> consultarStatusEDownload(@PathVariable String id) {

        // 1. Busca os metadados do vídeo diretamente no DynamoDB
        Optional<Object> videoOptional = videoRepositoryPort.findById(id);

        if (videoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        VideoMetadata video = (VideoMetadata) videoOptional.get();

        // 2. Se o status for COMPLETED, gera a URL do ZIP!
        if ("DONE".equalsIgnoreCase(video.status())) {

            // Passa o ID para o adapter. Ele mesmo vai concatenar o "processed/" e ".zip"
            String linkDownload = s3StorageAdapter.gerarUrlDownload(video.pedidoId());

            return ResponseEntity.ok(Map.of(
                    "pedidoId", video.pedidoId(),
                    "status", video.status(),
                    "mensagem", "O vídeo processado (.zip) está pronto para download!",
                    "downloadUrl", linkDownload
            ));
        }

        // 3. Se ainda estiver pendente ou deu erro, devolve sem o link
        return ResponseEntity.ok(Map.of(
                "pedidoId", video.pedidoId(),
                "status", video.status(),
                "mensagem", "O vídeo ainda está em processamento ou ocorreu um erro."
        ));
    }
}