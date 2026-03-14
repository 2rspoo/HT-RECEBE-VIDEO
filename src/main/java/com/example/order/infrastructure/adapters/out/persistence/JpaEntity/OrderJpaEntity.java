package com.example.order.infrastructure.adapters.out.persistence.JpaEntity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.example.order.domain.entities.Order;
import java.time.LocalDate;
import java.time.LocalTime;

@DynamoDBTable(tableName = "pedidos")
public class OrderJpaEntity {

    private Long id;
    private Long idcustomer;
    private String step;
    private LocalDate date;
    private LocalTime time;
    private Integer price;
    private String details;
    private String paymentStatus;

    public OrderJpaEntity() { }

    public OrderJpaEntity(Long id, Long idcustomer, String step, LocalDate date, LocalTime time, Integer price, String details, String paymentStatus) {
        this.id = id;
        this.idcustomer = idcustomer;
        this.step = step;
        this.date = date;
        this.time = time;
        this.price = price;
        this.details = details;
        this.paymentStatus = paymentStatus;
    }

    // --- Mapeamento para o DynamoDB ---

    @DynamoDBHashKey(attributeName = "id")
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @DynamoDBAttribute(attributeName = "idcustomer")
    public Long getIdcustomer() { return idcustomer; }
    public void setIdcustomer(Long idcustomer) { this.idcustomer = idcustomer; }

    @DynamoDBAttribute(attributeName = "step")
    public String getStep() { return step; }
    public void setStep(String step) { this.step = step; }

    // Uso de conversor personalizado para LocalDate
    @DynamoDBAttribute(attributeName = "date")
    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    // Uso de conversor personalizado para LocalTime
    @DynamoDBAttribute(attributeName = "time")
    @DynamoDBTypeConverted(converter = LocalTimeConverter.class)
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    @DynamoDBAttribute(attributeName = "price")
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    @DynamoDBAttribute(attributeName = "details")
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    @DynamoDBAttribute(attributeName = "paymentStatus")
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    // --- Conversores Estáticos para Java 8 Time API ---

    public static class LocalDateConverter implements DynamoDBTypeConverter<String, LocalDate> {
        @Override
        public String convert(LocalDate object) {
            return object != null ? object.toString() : null;
        }
        @Override
        public LocalDate unconvert(String object) {
            return object != null ? LocalDate.parse(object) : null;
        }
    }

    public static class LocalTimeConverter implements DynamoDBTypeConverter<String, LocalTime> {
        @Override
        public String convert(LocalTime object) {
            return object != null ? object.toString() : null;
        }
        @Override
        public LocalTime unconvert(String object) {
            return object != null ? LocalTime.parse(object) : null;
        }
    }

    // --- Métodos de conversão de Domínio ---

    public Order toDomain() {
        return new Order(this.id, this.idcustomer, this.step, this.date, this.time, this.price, this.details, this.paymentStatus);
    }

    public static OrderJpaEntity fromDomain(Order domain) {
        return new OrderJpaEntity(domain.getId(), domain.getIdcustomer(), domain.getStep(), domain.getDate(), domain.getTime(), domain.getPrice(), domain.getDetails(), domain.getPaymentStatus());
    }
}