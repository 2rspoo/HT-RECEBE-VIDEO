package com.example.order.infrastructure.adapters.in.web.Dtos;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MercadoPagoItemDTO {

    private String title;
    private String description;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    private Integer quantity;

    public MercadoPagoItemDTO() {
    }

    public MercadoPagoItemDTO(String title, String description, BigDecimal unitPrice, Integer quantity) {
        this.title = title;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}