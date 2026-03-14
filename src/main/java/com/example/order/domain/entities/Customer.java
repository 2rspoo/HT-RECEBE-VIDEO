package com.example.order.domain.entities;

public record Customer(
        String id,
        String name,
        String email,
        String cpf,
        String senha
) {}
