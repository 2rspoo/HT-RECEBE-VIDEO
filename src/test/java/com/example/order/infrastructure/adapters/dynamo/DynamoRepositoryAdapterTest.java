package com.example.order.infrastructure.adapters.dynamo;

import com.example.order.domain.entities.Video;
import com.example.order.domain.entities.VideoMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamoRepositoryAdapterTest {

    @Mock
    private DynamoDbClient dynamoDbClient;

    @InjectMocks
    private DynamoRepositoryAdapter adapter;

    @Captor
    private ArgumentCaptor<PutItemRequest> putItemCaptor;

    @Captor
    private ArgumentCaptor<GetItemRequest> getItemCaptor;

    @Captor
    private ArgumentCaptor<ScanRequest> scanCaptor;

    private static final String TABLE_NAME = "Pedidos";
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("Deve salvar um Video convertendo corretamente para PutItemRequest")
    void shouldSaveVideoSuccessfully() {
        // Arrange
        Video video = new Video("video-123", "user-456", "teste.mp4", "RECEIVED", now);

        // Act
        adapter.save(video);

        // Assert
        verify(dynamoDbClient, times(1)).putItem(putItemCaptor.capture());
        PutItemRequest request = putItemCaptor.getValue();

        assertEquals(TABLE_NAME, request.tableName());

        Map<String, AttributeValue> item = request.item();
        assertEquals("video-123", item.get("PedidoID").s());
        assertEquals("user-456", item.get("UserId").s());
        assertEquals("teste.mp4", item.get("FileName").s());
        assertEquals("RECEIVED", item.get("Status").s());
        assertEquals(now.toString(), item.get("CreatedAt").s());
    }

    @Test
    @DisplayName("Deve retornar VideoMetadata quando encontrar o registro pelo ID")
    void shouldFindByIdSuccessfully() {
        // Arrange
        String pedidoId = "video-123";

        // Criando uma resposta simulada (falsa) da AWS
        Map<String, AttributeValue> itemMap = Map.of(
                "PedidoID", AttributeValue.builder().s(pedidoId).build(),
                "UserId", AttributeValue.builder().s("user-456").build(),
                "FileName", AttributeValue.builder().s("teste.mp4").build(),
                "Status", AttributeValue.builder().s("PROCESSING").build(),
                "S3Url", AttributeValue.builder().s("s3://bucket/zip").build(),
                "CreatedAt", AttributeValue.builder().s(now.toString()).build()
        );

        GetItemResponse mockResponse = GetItemResponse.builder()
                .item(itemMap)
                .build();

        when(dynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResponse);

        // Act
        Optional<Object> result = adapter.findById(pedidoId);

        // Assert
        assertTrue(result.isPresent());
        VideoMetadata metadata = (VideoMetadata) result.get();
        assertEquals(pedidoId, metadata.pedidoId());
        assertEquals("s3://bucket/zip", metadata.s3Url());

        verify(dynamoDbClient, times(1)).getItem(getItemCaptor.capture());
        assertEquals(pedidoId, getItemCaptor.getValue().key().get("PedidoID").s());
    }

    @Test
    @DisplayName("Deve retornar Optional.empty quando o registro não existir no Dynamo")
    void shouldReturnEmptyWhenIdNotFound() {
        // Arrange
        GetItemResponse emptyResponse = GetItemResponse.builder().build(); // hasItem será false
        when(dynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(emptyResponse);

        // Act
        Optional<Object> result = adapter.findById("video-inexistente");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar um VideoMetadata mapeando inclusive a S3Url")
    void shouldUpdateVideoMetadataSuccessfully() {
        // Arrange
        VideoMetadata metadata = new VideoMetadata(
                "video-123", "user-456", "teste.mp4", "DONE", "s3://url-nova.zip", now
        );

        // Act
        adapter.update(metadata);

        // Assert
        verify(dynamoDbClient, times(1)).putItem(putItemCaptor.capture());
        Map<String, AttributeValue> item = putItemCaptor.getValue().item();

        assertEquals("DONE", item.get("Status").s());
        assertEquals("s3://url-nova.zip", item.get("S3Url").s()); // Garante que a URL foi mapeada
    }

    @Test
    @DisplayName("Deve buscar vídeos por UserId, mapear e ordenar pelos mais recentes")
    void shouldFindByUserIdAndSortByDateDescending() {
        // Arrange
        String userId = "user-456";
        LocalDateTime olderDate = now.minusDays(1);
        LocalDateTime newerDate = now;

        // Item 1 (Mais antigo)
        Map<String, AttributeValue> item1 = Map.of(
                "PedidoID", AttributeValue.builder().s("v1").build(),
                "UserId", AttributeValue.builder().s(userId).build(),
                "FileName", AttributeValue.builder().s("f1.mp4").build(),
                "Status", AttributeValue.builder().s("DONE").build(),
                "CreatedAt", AttributeValue.builder().s(olderDate.toString()).build()
        );

        // Item 2 (Mais novo)
        Map<String, AttributeValue> item2 = Map.of(
                "PedidoID", AttributeValue.builder().s("v2").build(),
                "UserId", AttributeValue.builder().s(userId).build(),
                "FileName", AttributeValue.builder().s("f2.mp4").build(),
                "Status", AttributeValue.builder().s("PROCESSING").build(),
                "CreatedAt", AttributeValue.builder().s(newerDate.toString()).build()
        );

        // A resposta do DynamoDB pode vir fora de ordem
        ScanResponse mockResponse = ScanResponse.builder()
                .items(item1, item2)
                .build();

        when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(mockResponse);

        // Act
        List<VideoMetadata> result = adapter.findByUserId(userId);

        // Assert
        assertEquals(2, result.size());

        // Verifica a ordenação: O item2 (mais novo) deve estar na posição 0
        assertEquals("v2", result.get(0).pedidoId());
        assertEquals("v1", result.get(1).pedidoId());

        // Verifica o filtro do ScanRequest
        verify(dynamoDbClient, times(1)).scan(scanCaptor.capture());
        assertEquals(userId, scanCaptor.getValue().expressionAttributeValues().get(":userId").s());
    }
}