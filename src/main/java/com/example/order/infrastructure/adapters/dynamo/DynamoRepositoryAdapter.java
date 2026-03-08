package com.example.order.infrastructure.adapters.dynamo;

import com.example.order.application.ports.out.VideoRepositoryPort;
import com.example.order.domain.entities.Video;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class DynamoRepositoryAdapter implements VideoRepositoryPort {
    private final DynamoDbClient dynamoDbClient;

    // Defina o nome da tabela aqui
    private static final String TABLE_NAME = "Pedidos";

    public DynamoRepositoryAdapter(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void save(Video video) {
        Map<String, AttributeValue> item = new HashMap<>();

        // ADAPTAÇÃO DAS CHAVES:
        // Verifique se na sua tabela 'Pedidos' as chaves se chamam PK/SK ou Id/Status
        item.put("Id", AttributeValue.builder().s(video.id()).build());
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
}