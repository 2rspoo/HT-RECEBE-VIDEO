package com.example.order.webhook;

public interface ProcessPaymentWebhookUseCase {
    void execute(Long orderId, String paymentStatus);
}
