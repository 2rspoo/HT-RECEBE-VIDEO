package com.example.order.application.ports.out;

import com.example.order.domain.entities.Customer;
import java.util.Optional;

public interface CustomerRepositoryPort {
    // Busca o cliente pelo CPF para recuperar nome e e-mail
    Optional<Customer> findByCpf(String cpf);
}