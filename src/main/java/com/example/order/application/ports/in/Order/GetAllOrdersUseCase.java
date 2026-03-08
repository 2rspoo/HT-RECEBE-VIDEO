// src/main/java/com/example/order/application/ports/in/GetAllOrdersUseCase.java
package com.example.order.application.ports.in.Order;

import com.example.order.domain.entities.Order;
import java.util.List;

public interface GetAllOrdersUseCase {
    List<Order> execute();
}