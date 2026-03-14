package com.example.order.application.ports.out;

import com.example.order.domain.entities.Video;
import com.example.order.domain.entities.VideoMetadata;

import java.util.List;
import java.util.Optional;

// application/ports/output/VideoRepositoryPort.java
public interface VideoRepositoryPort {
    void save(Video video);
    void update(VideoMetadata video);

    <T> Optional<Object> findById(String pedidoId);

    List<VideoMetadata> findByUserId(String userId);
}