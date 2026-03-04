// src/main/java/com/example/order/infrastructure/adapters/in/web/dtos/PaymentWebhookRequestDTO.java
package com.example.order.webhook;

public class PaymentWebhookRequestDTO {
    private Long orderId;
    private String paymentStatus; // "APROVADO", "RECUSADO", "PENDENTE"

    // Getters e Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }


}