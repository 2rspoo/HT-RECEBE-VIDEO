package com.example.order.infrastructure.adapters.dynamo;

import com.example.order.application.ports.out.VideoRepositoryPort;
import com.example.order.domain.entities.Video;
import com.example.order.domain.entities.VideoMetadata;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class DynamoRepositoryAdapter implements VideoRepositoryPort {
    private final DynamoDbClient dynamoDbClient;

    // Defina o nome da tabela aqui
    private static final String TABLE_NAME = "Pedidos";

    public DynamoRepositoryAdapter(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void save(@org.jetbrains.annotations.NotNull Video video) {
        Map<String, AttributeValue> item = new HashMap<>();

        // ADAPTAÇÃO DAS CHAVES:
        // Verifique se na sua tabela 'Pedidos' as chaves se chamam PK/SK ou Id/Status
        item.put("PedidoID", AttributeValue.builder().s(video.id()).build());
        item.put("UserId", AttributeValue.builder().s(video.userId()).build());
        item.put("Status", AttributeValue.builder().s(video.status()).build());
        item.put("FileName", AttributeValue.builder().s(video.fileName()).build());
        item.put("CreatedAt", AttributeValue.builder().s(video.createdAt().toString()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }
    @Override
    public Optional<Object> findById(String pedidoId) {
        software.amazon.awssdk.services.dynamodb.model.GetItemRequest request = software.amazon.awssdk.services.dynamodb.model.GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("PedidoID", AttributeValue.builder().s(pedidoId).build()))
                .build();

        software.amazon.awssdk.services.dynamodb.model.GetItemResponse response = dynamoDbClient.getItem(request);

        if (!response.hasItem()) {
            return java.util.Optional.empty();
        }

        Map<String, AttributeValue> item = response.item();
        return java.util.Optional.of(new VideoMetadata(
                item.get("PedidoID").s(),
                item.get("UserId").s(),
                item.get("FileName").s(),
                item.get("Status").s(),
                item.containsKey("S3Url") ? item.get("S3Url").s() : null,
                java.time.LocalDateTime.parse(item.get("CreatedAt").s())
        ));
    }

    @Override
    public void update(VideoMetadata video) {
        Map<String, AttributeValue> item = new HashMap<>();

        // No DynamoDB, um PutItem com a mesma Chave Primária atualiza/sobrescreve o registro.
        item.put("PedidoID", AttributeValue.builder().s(video.pedidoId()).build());
        item.put("UserId", AttributeValue.builder().s(video.userId()).build());
        item.put("Status", AttributeValue.builder().s(video.status()).build());
        item.put("FileName", AttributeValue.builder().s(video.fileName()).build());
        item.put("CreatedAt", AttributeValue.builder().s(video.createdAt().toString()).build());

        if (video.s3Url() != null) {
            item.put("S3Url", AttributeValue.builder().s(video.s3Url()).build());
        }

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    @Override
    public java.util.List<VideoMetadata> findByUserId(String userId) {
        software.amazon.awssdk.services.dynamodb.model.ScanRequest scanRequest = software.amazon.awssdk.services.dynamodb.model.ScanRequest.builder()
                .tableName(TABLE_NAME)
                .filterExpression("UserId = :userId")
                .expressionAttributeValues(Map.of(":userId", AttributeValue.builder().s(userId).build()))
                .build();

        software.amazon.awssdk.services.dynamodb.model.ScanResponse response = dynamoDbClient.scan(scanRequest);

        java.util.List<VideoMetadata> videos = new java.util.ArrayList<>();

        for (Map<String, AttributeValue> item : response.items()) {
            videos.add(new VideoMetadata(
                    item.get("PedidoID").s(),
                    item.get("UserId").s(),
                    item.get("FileName").s(),
                    item.get("Status").s(),
                    item.containsKey("S3Url") ? item.get("S3Url").s() : null,
                    java.time.LocalDateTime.parse(item.get("CreatedAt").s())
            ));
        }

        // Opcional: Ordenar pela data de criação (mais recentes primeiro)
        videos.sort((v1, v2) -> v2.createdAt().compareTo(v1.createdAt()));

        return videos;
    }




}