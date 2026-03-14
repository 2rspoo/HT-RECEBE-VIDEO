// src/main/java/com/example/order/application/usecases/GetOrdersByStepService.java
package com.example.order.application.usecases.Order;
import com.example.order.application.ports.in.Order.GetOrdersByStepUseCase;
import com.example.order.application.ports.out.OrderRepositoryPort;
import com.example.order.domain.entities.Order;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GetOrdersByStepService implements GetOrdersByStepUseCase {
    private final OrderRepositoryPort orderRepositoryPort;
    public GetOrdersByStepService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }
    @Override
    public List<Order> execute(String step) {
        return orderRepositoryPort.findByStep(step);
    }
}