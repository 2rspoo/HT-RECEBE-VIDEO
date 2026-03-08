// src/main/java/com/example/order/application/ports/in/CancelOrderUseCase.java
package com.example.order.application.ports.in.Order;

import com.example.order.domain.entities.Order;

public interface CancelOrderUseCase {
    Order execute(Long orderId);
}