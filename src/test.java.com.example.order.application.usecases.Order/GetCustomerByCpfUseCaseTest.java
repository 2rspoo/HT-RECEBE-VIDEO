package com.example.order.application.usecases.Order;

import com.example.order.application.ports.out.CustomerRepositoryPort;
import com.example.order.domain.entities.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCustomerByCpfUseCaseTest {

    @Mock
    private CustomerRepositoryPort customerRepositoryPort;

    @InjectMocks
    private GetCustomerByCpfUseCase useCase;

    @Test
    @DisplayName("Deve retornar um Optional com o cliente quando o CPF existir")
    void shouldReturnCustomerWhenCpfExists() {
        // Arrange
        String cpf = "12345678900";
        // Criamos um mock da entidade Customer para não depender do construtor real dela
        Customer mockCustomer = mock(Customer.class);

        // Ensina o mock do repositório a devolver o cliente embrulhado num Optional
        when(customerRepositoryPort.findByCpf(cpf)).thenReturn(Optional.of(mockCustomer));

        // Act
        Optional<Customer> result = useCase.execute(cpf);

        // Assert
        assertTrue(result.isPresent(), "O Optional não deveria estar vazio");
        assertEquals(mockCustomer, result.get(), "O cliente retornado deve ser o mesmo do repositório");

        // Garante que chamou a base de dados com o CPF exato
        verify(customerRepositoryPort, times(1)).findByCpf(cpf);
    }

    @Test
    @DisplayName("Deve retornar um Optional vazio quando o CPF não existir")
    void shouldReturnEmptyWhenCpfDoesNotExist() {
        // Arrange
        String cpf = "00000000000";

        // Ensina o mock a retornar um Optional.empty() simulando que não achou no banco
        when(customerRepositoryPort.findByCpf(cpf)).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = useCase.execute(cpf);

        // Assert
        assertTrue(result.isEmpty(), "O Optional deveria estar vazio");

        verify(customerRepositoryPort, times(1)).findByCpf(cpf);
    }
}