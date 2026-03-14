package com.example.order.application.usecases.Order;

import com.example.order.application.ports.out.VideoRepositoryPort;
import com.example.order.domain.entities.VideoMetadata;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListarVideosUseCase {

    private final VideoRepositoryPort videoRepositoryPort;

    public ListarVideosUseCase(VideoRepositoryPort videoRepositoryPort) {
        this.videoRepositoryPort = videoRepositoryPort;
    }

    public List<VideoMetadata> execute(String userId) {
        return videoRepositoryPort.findByUserId(userId);
    }
}