package com.example.order.infrastructure.adapters.sqs;

import com.example.order.application.ports.out.VideoQueuePort;
import com.example.order.domain.entities.Video;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SqsQueueAdapter implements VideoQueuePort {
    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.url}")
    private String queueUrl;

    public SqsQueueAdapter(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    public void sendToProcess(Video video) {
        sqsTemplate.send(queueUrl, video);
    }
}