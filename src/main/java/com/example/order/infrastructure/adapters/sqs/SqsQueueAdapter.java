package com.example.order.infrastructure.adapters.sqs;

import com.example.order.application.ports.out.VideoQueuePort;
import com.example.order.domain.entities.Video;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SqsQueueAdapter implements VideoQueuePort {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.url}")
    private String queueUrl;

    // Injeta o ObjectMapper do Spring
    public SqsQueueAdapter(SqsTemplate sqsTemplate, ObjectMapper objectMapper) {
        this.sqsTemplate = sqsTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendToProcess(Video video) {
        try {
            // [O PULO DO GATO]: Ao converter a entidade para String manualmente,
            // o Spring NÃO adiciona o cabeçalho JavaType tóxico à mensagem SQS!
            String jsonPayload = objectMapper.writeValueAsString(video);

            sqsTemplate.send(queueUrl, jsonPayload);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar mensagem SQS para envio", e);
        }
    }
}