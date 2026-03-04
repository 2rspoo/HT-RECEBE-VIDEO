package com.example.order.infrastructure.adapters.out.persistence.JpaAdapter; // Você pode renomear o pacote depois

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.order.application.ports.out.OrderRepositoryPort;
import com.example.order.domain.entities.Order;
import com.example.order.infrastructure.adapters.out.persistence.JpaEntity.OrderJpaEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderDynamoAdapter implements OrderRepositoryPort {

    private final DynamoDBMapper dynamoDBMapper;

    public OrderDynamoAdapter(AmazonDynamoDB amazonDynamoDB) {
        this.dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
    }

    @Override
    public List<Order> findAll() {
        return dynamoDBMapper.scan(OrderJpaEntity.class, new DynamoDBScanExpression())
                .stream().map(OrderJpaEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStep(String step) {
        // No DynamoDB, buscas por campos que não são chave exigem um Scan com filtro
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(step));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("step = :v1")
                .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(OrderJpaEntity.class, scanExpression)
                .stream().map(OrderJpaEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Order> findById(Long id) {
        OrderJpaEntity entity = dynamoDBMapper.load(OrderJpaEntity.class, id);
        return Optional.ofNullable(entity).map(OrderJpaEntity::toDomain);
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(System.currentTimeMillis());
        }
        OrderJpaEntity entity = OrderJpaEntity.fromDomain(order);
        dynamoDBMapper.save(entity);
        return entity.toDomain();
    }
}