// src/main/java/com/example/order/application/usecases/Order/GetAllOrdersService.java
package com.example.order.application.usecases.Order;

import com.example.order.application.ports.in.Order.GetAllOrdersUseCase;
import com.example.order.application.ports.out.OrderRepositoryPort;
import com.example.order.domain.entities.Order;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllOrdersService implements GetAllOrdersUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public GetAllOrdersService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public List<Order> execute() {
        return orderRepositoryPort.findAll().stream()
                // 1. Filtra para remover pedidos com status FINALIZADO ou CANCELADO
                .filter(order -> !"FINALIZADO".equals(order.getStep()) && !"CANCELADO".equals(order.getStep()))
                // 2. Ordena usando um único Comparator
                .sorted(Comparator.comparingInt(this::getPriorityForStatus)
                        .thenComparing(Order::getDate)
                        .thenComparing(Order::getTime))
                // 3. Coleta o resultado em uma lista
                .collect(Collectors.toList());
    }

    private int getPriorityForStatus(Order order) {
        return switch (order.getStep()) {
            case "PRONTO" -> 1;
            case "PREPARO" -> 2;
            case "RECEBIDO" -> 3;
            default -> 4; // Qualquer outro status não listado
        };
    }
}