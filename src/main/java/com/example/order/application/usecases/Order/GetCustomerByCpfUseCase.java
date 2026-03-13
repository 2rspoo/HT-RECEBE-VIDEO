package com.example.order.application.usecases.Order;


import com.example.order.application.ports.out.CustomerRepositoryPort;
import com.example.order.domain.entities.Customer;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class GetCustomerByCpfUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;

    public GetCustomerByCpfUseCase(CustomerRepositoryPort customerRepositoryPort) {
        this.customerRepositoryPort = customerRepositoryPort;
    }


    public Optional<Customer> execute(String cpf) {
        return customerRepositoryPort.findByCpf(cpf);
    }
}
