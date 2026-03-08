package com.example.order.application.ports.out;

import com.example.order.domain.entities.Video;

// application/ports/output/VideoRepositoryPort.java
public interface VideoRepositoryPort {
    void save(Video video);
}