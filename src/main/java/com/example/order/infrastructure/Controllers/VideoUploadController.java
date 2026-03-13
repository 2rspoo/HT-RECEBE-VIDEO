package com.example.order.infrastructure.Controllers;

import com.example.order.application.usecases.Order.UploadVideoUseCase;
import com.example.order.application.usecases.Order.ListarVideosUseCase;
import com.example.order.domain.entities.VideoMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// infrastructure/controllers/VideoUploadController.java
@RestController
@RequestMapping("/v1/videos")
public class VideoUploadController {
    private final UploadVideoUseCase uploadUseCase;

    private final ListarVideosUseCase listarVideosUseCase;



    public VideoUploadController(UploadVideoUseCase uploadUseCase, ListarVideosUseCase listarVideosUseCase) {
        this.uploadUseCase = uploadUseCase;
        this.listarVideosUseCase = listarVideosUseCase;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file, @RequestHeader("X-User-Id") String userId) throws IOException, IOException {
        String videoId = uploadUseCase.execute(userId, file.getOriginalFilename(), file.getBytes());
        return ResponseEntity.ok(videoId);
    }


    // NOVO ENDPOINT: Lista os vídeos pelo CPF do usuário
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<VideoMetadata>> listarPorUsuario(@PathVariable String userId) {
        List<VideoMetadata> videos = listarVideosUseCase.execute(userId);
        return ResponseEntity.ok(videos);
    }
}