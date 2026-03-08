// src/main/java/com/example/order/domain/entities/Order.java
package com.example.order.domain.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Order {

    private Long id;
    private Long idcustomer;
    private String step;
    private LocalDate date;
    private LocalTime time;
    private Integer price;
    private String details;
    private String paymentStatus; // <-- Novo campo para o status de pagamento

    // Construtor para a criação de um novo pedido.
    // O status de pagamento é inicializado como "PENDENTE" por padrão.
    public Order(Long id, Long idcustomer, String step, Integer price, String details) {
        this.idcustomer = idcustomer;
        this.step = "RECEBIDO";
        this.price = price;
        this.details = details;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.paymentStatus = "PENDENTE"; // <-- Status inicial do pagamento
    }

    // Construtor completo, usado para reconstituir o objeto Order a partir do banco de dados.
    public Order(Long id, Long idcustomer, String step, LocalDate date, LocalTime time, Integer price, String details, String paymentStatus) {
        this.id = id;
        this.idcustomer = idcustomer;
        this.step = step;
        this.date = date;
        this.time = time;
        this.price = price;
        this.details = details;
        this.paymentStatus = paymentStatus;
    }

    public Order() {

    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdcustomer() { return idcustomer; }
    public void setIdcustomer(Long idcustomer) { this.idcustomer = idcustomer; }
    public String getStep() { return step; }
    public void setStep(String step) { this.step = step; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getPaymentStatus() { return paymentStatus; } // <-- Novo getter
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; } // <-- Novo setter

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setQrCodeData(Object qrData) {

    }
}