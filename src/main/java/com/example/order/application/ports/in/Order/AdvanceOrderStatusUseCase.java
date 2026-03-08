// src/main/java/com/example/order/application/ports/in/AdvanceOrderStatusUseCase.java
package com.example.order.application.ports.in.Order;

import com.example.order.domain.entities.Order;

public interface AdvanceOrderStatusUseCase {
    Order execute(Long orderId);
}