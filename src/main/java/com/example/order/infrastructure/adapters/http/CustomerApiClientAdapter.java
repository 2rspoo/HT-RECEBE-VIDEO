package com.example.order.infrastructure.adapters.http;

import com.example.order.application.ports.out.CustomerRepositoryPort;
import com.example.order.domain.entities.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class CustomerApiClientAdapter implements CustomerRepositoryPort {

    private final RestTemplate restTemplate;

    // Puxa a URL do LoadBalancer do application.properties
    @Value("${api.clientes.url}")
    private String customerApiUrl;

    public CustomerApiClientAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Customer> findByCpf(String cpf) {
        try {
            // Chama a sua aplicação de clientes (usando o CustomerController que você mostrou antes)
            String url = customerApiUrl + "/customer/cpf/" + cpf;

            // O RestTemplate converte o JSON retornado diretamente para o record Customer
            Customer customer = restTemplate.getForObject(url, Customer.class);

            return Optional.ofNullable(customer);
        } catch (Exception e) {
            System.err.println(">>> Erro ao buscar dados do cliente na API externa: " + e.getMessage());
            return Optional.empty();
        }
    }
}
