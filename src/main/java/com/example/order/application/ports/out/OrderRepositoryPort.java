// src/main/java/com/example/order/application/ports/out/OrderRepositoryPort.java
package com.example.order.application.ports.out;
import com.example.order.domain.entities.Order;
import java.util.List;
import java.util.Optional;
public interface OrderRepositoryPort {
    List<Order> findAll();
    List<Order> findByStep(String step); // findByStep em vez de findByStatus
    Optional<Order> findById(Long id);
    Order save(Order order);
}