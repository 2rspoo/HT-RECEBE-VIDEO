package com.example.order.infrastructure.adapters.http;

import com.example.order.domain.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerApiClientAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CustomerApiClientAdapter adapter;

    private final String customerApiUrl = "http://api-clientes.internal";

    @BeforeEach
    void setUp() {
        // Injeta o valor da URL configurada no properties para dentro da nossa classe mockada
        ReflectionTestUtils.setField(adapter, "customerApiUrl", customerApiUrl);
    }

    @Test
    @DisplayName("Deve buscar o cliente com sucesso na API externa e retornar um Optional preenchido")
    void shouldFindCustomerSuccessfully() {
        // Arrange
        String cpf = "12345678900";
        String expectedUrl = customerApiUrl + "/customer/cpf/" + cpf;

        Customer mockCustomer = new Customer("id-123", "Maria", "maria@email.com", cpf, "senha123");

        // Simula o RestTemplate retornando o objeto populado
        when(restTemplate.getForObject(expectedUrl, Customer.class)).thenReturn(mockCustomer);

        // Act
        Optional<Customer> result = adapter.findByCpf(cpf);

        // Assert
        assertTrue(result.isPresent(), "O Optional não deve estar vazio");
        assertEquals(mockCustomer, result.get(), "O cliente retornado deve ser o mesmo devolvido pela API");

        // Garante que a URL foi montada corretamente
        verify(restTemplate, times(1)).getForObject(expectedUrl, Customer.class);
    }

    @Test
    @DisplayName("Deve retornar Optional.empty quando a API retornar nulo (cliente não encontrado)")
    void shouldReturnEmptyWhenApiReturnsNull() {
        // Arrange
        String cpf = "00000000000";
        String expectedUrl = customerApiUrl + "/customer/cpf/" + cpf;

        // Simula o RestTemplate não encontrando o objeto e retornando null
        when(restTemplate.getForObject(expectedUrl, Customer.class)).thenReturn(null);

        // Act
        Optional<Customer> result = adapter.findByCpf(cpf);

        // Assert
        assertTrue(result.isEmpty(), "O Optional deve estar vazio quando a API não retornar nada");
        verify(restTemplate, times(1)).getForObject(expectedUrl, Customer.class);
    }

    @Test
    @DisplayName("Deve capturar a exceção e retornar Optional.empty quando a requisição falhar")
    void shouldReturnEmptyWhenApiThrowsException() {
        // Arrange
        String cpf = "12345678900";
        String expectedUrl = customerApiUrl + "/customer/cpf/" + cpf;

        // Simula o RestTemplate lançando um erro (ex: API fora do ar, timeout ou Erro 500)
        when(restTemplate.getForObject(expectedUrl, Customer.class))
                .thenThrow(new RestClientException("Connection refused"));

        // Act
        Optional<Customer> result = adapter.findByCpf(cpf);

        // Assert
        assertTrue(result.isEmpty(), "O Optional deve estar vazio caso ocorra erro na chamada");

        // Garante que a chamada foi feita antes de falhar
        verify(restTemplate, times(1)).getForObject(expectedUrl, Customer.class);
    }
}