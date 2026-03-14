// src/main/java/com/example/order/application/ports/in/FindOrderUseCase.java
package com.example.order.application.ports.in.Order;

import com.example.order.domain.entities.Order;
import java.util.Optional;

public interface FindOrderUseCase {
    Optional<Order> execute(Long orderId);
}