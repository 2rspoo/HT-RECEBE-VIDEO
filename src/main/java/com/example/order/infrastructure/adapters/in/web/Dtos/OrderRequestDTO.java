// src/main/java/com/example/order/infrastructure/adapters/in/web/dtos/OrderRequestDTO.java
package com.example.order.infrastructure.adapters.in.web.Dtos;

import com.example.order.domain.entities.Order;
import java.time.LocalDate;
import java.time.LocalTime;

public class OrderRequestDTO {
    private Long idcustomer;
    private String step;
    private Integer price;
    private String details;

    public Long getIdcustomer() { return idcustomer; }
    public void setIdcustomer(Long idcustomer) { this.idcustomer = idcustomer; }
    public String getStep() { return step; }
    public void setStep(String step) { this.step = step; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    // Método estático para converter DTO em Domínio.
    // Ele precisa receber a própria instância do DTO como parâmetro.
    public static Order toDomain(OrderRequestDTO dto) {
        Order newOrder = new Order();
        newOrder.setIdcustomer(dto.getIdcustomer());
        newOrder.setStep("RECEBIDO");
        newOrder.setDate(LocalDate.now()); // Adicione a data atual
        newOrder.setTime(LocalTime.now()); // Adicione a hora atual
        newOrder.setPrice(dto.getPrice());
        newOrder.setDetails(dto.getDetails());
        newOrder.setPaymentStatus("PENDENTE"); // Defina o status inicial de pagamento
        return newOrder;
    }
}