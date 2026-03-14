package com.example.order.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomerTest {

    @Test
    @DisplayName("Deve instanciar um Customer e manter os valores corretamente")
    void shouldInstantiateCustomerCorrectly() {
        // Arrange
        String id = "uuid-1234";
        String name = "João da Silva";
        String email = "joao@exemplo.com";
        String cpf = "12345678900";
        String senha = "senhaForte123";

        // Act
        Customer customer = new Customer(id, name, email, cpf, senha);

        // Assert
        assertNotNull(customer, "O objeto Customer não deveria ser nulo");
        assertEquals(id, customer.id(), "O ID deve ser igual ao passado no construtor");
        assertEquals(name, customer.name(), "O nome deve ser igual ao passado no construtor");
        assertEquals(email, customer.email(), "O email deve ser igual ao passado no construtor");
        assertEquals(cpf, customer.cpf(), "O CPF deve ser igual ao passado no construtor");
        assertEquals(senha, customer.senha(), "A senha deve ser igual a passada no construtor");
    }
}