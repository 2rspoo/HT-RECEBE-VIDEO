package com.example.order.infrastructure.Controllers;

import com.example.order.application.usecases.Order.UploadVideoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// infrastructure/controllers/VideoUploadController.java
@RestController
@RequestMapping("/v1/videos")
public class VideoUploadController {
    private final UploadVideoUseCase uploadUseCase;

    public VideoUploadController(UploadVideoUseCase uploadUseCase) {
        this.uploadUseCase = uploadUseCase;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file, @RequestHeader("X-User-Id") String userId) throws IOException, IOException {
        String videoId = uploadUseCase.execute(userId, file.getOriginalFilename(), file.getBytes());
        return ResponseEntity.ok(videoId);
    }
}