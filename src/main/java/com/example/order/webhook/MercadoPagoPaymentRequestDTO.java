package com.example.order.webhook;

import com.example.order.infrastructure.adapters.in.web.Dtos.MercadoPagoItemDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public class MercadoPagoPaymentRequestDTO {

    @JsonProperty("external_reference")
    private String externalReference;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    private String description;

    private List<MercadoPagoItemDTO> items;

    public MercadoPagoPaymentRequestDTO() {
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MercadoPagoItemDTO> getItems() {
        return items;
    }

    public void setItems(List<MercadoPagoItemDTO> items) {
        this.items = items;
    }
}