// src/main/java/com/example/order/application/ports/in/GetOrdersByStepUseCase.java
package com.example.order.application.ports.in.Order;

import com.example.order.domain.entities.Order;
import java.util.List;

public interface GetOrdersByStepUseCase {
    List<Order> execute(String status);
}