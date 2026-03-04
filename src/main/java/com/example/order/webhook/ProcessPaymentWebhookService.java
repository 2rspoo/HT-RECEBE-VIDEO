// src/main/java/com/example/order/application/ports/in/ProcessPaymentWebhookUseCase.java
package com.example.order.webhook;

import com.example.order.application.ports.out.OrderRepositoryPort;
import com.example.order.domain.entities.Order;
import com.example.order.shared.exceptions.OrderNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProcessPaymentWebhookService implements ProcessPaymentWebhookUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public ProcessPaymentWebhookService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override

    public void execute(Long orderId, String paymentStatus) {
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado com ID: " + orderId));

        // Atualiza o status do pagamento
        order.setPaymentStatus(paymentStatus);

        // Regra de Negócio: Se o pagamento for APROVADO, avança o status do pedido
        if ("APROVADO".equals(paymentStatus)) {
            order.setStep("PREPARO"); // Ou o próximo passo na sua lógica
        } else if ("RECUSADO".equals(paymentStatus)) {
            order.setStep("CANCELADO");
        }

        orderRepositoryPort.save(order);
    }
}