package com.example.order.application.usecases.Order;

import com.example.order.application.ports.out.OrderRepositoryPort;
import com.example.order.domain.entities.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOrdersByStepServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private GetOrdersByStepService getOrdersByStepService;

    @Test
    @DisplayName("Deve retornar lista de pedidos filtrada pelo status informado")
    void deveRetornarPedidosPorStep() {
        // Arrange
        String statusProcurado = "PRONTO";

        Order order1 = new Order();
        order1.setId(1L);
        order1.setStep(statusProcurado);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setStep(statusProcurado);

        when(orderRepositoryPort.findByStep(statusProcurado))
                .thenReturn(Arrays.asList(order1, order2));

        // Act
        List<Order> resultado = getOrdersByStepService.execute(statusProcurado);

        // Assert 11
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Order::getStep).containsOnly(statusProcurado);

        // Verifica se o método do repositório foi chamado com o parâmetro correto
        verify(orderRepositoryPort, times(1)).findByStep(statusProcurado);
    }

    @Test
    @DisplayName("Deve retornar lista vazia se não houver pedidos com o status informado")
    void deveRetornarVazioSeNaoEncontrar() {
        // Arrange
        String statusInexistente = "STATUS_MALUCO";
        when(orderRepositoryPort.findByStep(statusInexistente)).thenReturn(Collections.emptyList());

        // Act
        List<Order> resultado = getOrdersByStepService.execute(statusInexistente);

        // Assert
        assertThat(resultado).isEmpty();
        verify(orderRepositoryPort, times(1)).findByStep(statusInexistente);
    }
}