// src/main/java/com/example/order/application/usecases/AdvanceOrderStatusService.java
package com.example.order.application.usecases.Order;
import com.example.order.application.ports.in.Order.AdvanceOrderStatusUseCase;
import com.example.order.application.ports.out.OrderRepositoryPort;
import com.example.order.domain.entities.Order;
import com.example.order.shared.exceptions.OrderNotFoundException;
import org.springframework.stereotype.Service;
//import jakarta.transaction.Transactional;

@Service
public class AdvanceOrderStatusService implements AdvanceOrderStatusUseCase {
    private final OrderRepositoryPort orderRepositoryPort;
    public AdvanceOrderStatusService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }
    @Override

    public Order execute(Long orderId) {
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        if ("RECEBIDO".equals(order.getStep())) {
            throw new IllegalArgumentException("Aguardando pagamento " );
        } else if ("PREPARO".equals(order.getStep())) {
            order.setStep("PRONTO");
        } else if ("PRONTO".equals(order.getStep())) {
            order.setStep("FINALIZADO");
        } else {
            throw new IllegalArgumentException("Cannot advance status from: " + order.getStep());
        }
        return orderRepositoryPort.save(order);
    }
}