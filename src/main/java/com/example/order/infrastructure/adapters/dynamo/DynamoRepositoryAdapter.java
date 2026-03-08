// infrastructure/adapters/dynamo/DynamoRepositoryAdapter.java
@Component
public class DynamoRepositoryAdapter implements VideoRepositoryPort {
    private final DynamoDbClient dynamoDbClient;

    public void save(Video video) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("PK", AttributeValue.builder().s("USER#" + video.userId()).build());
        item.put("SK", AttributeValue.builder().s("VIDEO#" + video.id()).build());
        item.put("Status", AttributeValue.builder().s(video.status()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("VideosTable")
                .item(item)
                .build();
        dynamoDbClient.putItem(request);
    }
}