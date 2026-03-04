// src/main/java/com/example/order/infrastructure/adapters/in/web/dtos/OrderResponseDTO.java
package com.example.order.infrastructure.adapters.in.web.Dtos;

import com.example.order.domain.entities.Order;
import java.time.LocalDate;
import java.time.LocalTime;

public class OrderResponseDTO {

    private Long id;
    private Long idcustomer;
    private String step;
    private LocalDate date;
    private LocalTime time;
    private Integer price;
    private String details;
    private String paymentStatus; // <-- Novo campo

    public OrderResponseDTO(Long id, Long idcustomer, String step, LocalDate date, LocalTime time, Integer price, String details, String paymentStatus) {
        this.id = id;
        this.idcustomer = idcustomer;
        this.step = step;
        this.date = date;
        this.time = time;
        this.price = price;
        this.details = details;
        this.paymentStatus = paymentStatus;
    }

    public static OrderResponseDTO fromDomain(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getIdcustomer(),
                order.getStep(),
                order.getDate(),
                order.getTime(),
                order.getPrice(),
                order.getDetails(),
                order.getPaymentStatus() // <-- Incluindo o novo campo
        );
    }

    // Getters
    public Long getId() { return id; }
    public Long getIdcustomer() { return idcustomer; }
    public String getStep() { return step; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public Integer getPrice() { return price; }
    public String getDetails() { return details; }
    public String getPaymentStatus() { return paymentStatus; } // <-- Novo getter
}