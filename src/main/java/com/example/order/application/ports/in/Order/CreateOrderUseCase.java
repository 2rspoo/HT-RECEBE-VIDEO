package com.example.order.application.ports.in.Order;

import com.example.order.domain.entities.Order;
import com.example.order.webhook.MercadoPagoPaymentResponseDTO; // <-- Importar o DTO

public interface CreateOrderUseCase {
    MercadoPagoPaymentResponseDTO execute(Order order); // <-- Tipo de retorno alterado
}