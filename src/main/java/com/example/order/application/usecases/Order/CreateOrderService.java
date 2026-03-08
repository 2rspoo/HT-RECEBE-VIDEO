// src/main/java/com/example/order/application/usecases/Order/CreateOrderService.java
package com.example.order.application.usecases.Order;

import com.example.order.application.ports.in.Order.CreateOrderUseCase;
import com.example.order.application.ports.out.OrderRepositoryPort;
import com.example.order.webhook.PaymentServicePort;
import com.example.order.webhook.ProcessPaymentWebhookUseCase;
import com.example.order.domain.entities.Order;
import com.example.order.webhook.MercadoPagoPaymentResponseDTO;
import org.springframework.stereotype.Service;
//import jakarta.transaction.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final PaymentServicePort paymentServicePort;
    private final ProcessPaymentWebhookUseCase processPaymentWebhookUseCase; // Adicione 'final' aqui

    // CORREÇÃO: Adicione a injeção de dependência no construtor
    public CreateOrderService(OrderRepositoryPort orderRepositoryPort, PaymentServicePort paymentServicePort, ProcessPaymentWebhookUseCase processPaymentWebhookUseCase) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.paymentServicePort = paymentServicePort;
        this.processPaymentWebhookUseCase = processPaymentWebhookUseCase; // Atribua a dependência
    }

    @Override

    public MercadoPagoPaymentResponseDTO execute(Order order) {
        Order savedOrder = orderRepositoryPort.save(order);



        MercadoPagoPaymentResponseDTO qrCodeResponse = paymentServicePort.createQrCodePayment(savedOrder);

        processPaymentWebhookUseCase.execute(savedOrder.getId(), qrCodeResponse.getStatus());



        return qrCodeResponse;
    }
}