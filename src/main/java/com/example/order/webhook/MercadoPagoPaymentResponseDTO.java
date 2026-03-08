package com.example.order.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MercadoPagoPaymentResponseDTO {

    private String status;
    private String message;

    @JsonProperty("qr_data")
    private String qrData;

    public MercadoPagoPaymentResponseDTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }
}