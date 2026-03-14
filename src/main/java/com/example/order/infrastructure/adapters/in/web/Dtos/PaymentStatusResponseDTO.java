// src/main/java/com/example/order/infrastructure/adapters/in/web/dtos/PaymentStatusResponseDTO.java
package com.example.order.infrastructure.adapters.in.web.Dtos;

public class PaymentStatusResponseDTO {
    private String paymentStatus;

    public PaymentStatusResponseDTO(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}