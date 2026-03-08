package com.example.order.application.ports.out;

import com.example.order.domain.entities.Video;

// application/ports/output/VideoQueuePort.java
public interface VideoQueuePort {
    void sendToProcess(Video video);
}