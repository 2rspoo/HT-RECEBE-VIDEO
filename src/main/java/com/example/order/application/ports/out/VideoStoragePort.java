package com.example.order.application.ports.out;

// application/ports/output/VideoStoragePort.java
public interface VideoStoragePort {
    void upload(String videoId, byte[] data);
}