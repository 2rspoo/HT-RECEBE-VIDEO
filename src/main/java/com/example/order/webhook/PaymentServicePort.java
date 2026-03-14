package com.example.order.webhook;

import com.example.order.domain.entities.Order;


public interface PaymentServicePort {
    MercadoPagoPaymentResponseDTO createQrCodePayment(Order order);
}