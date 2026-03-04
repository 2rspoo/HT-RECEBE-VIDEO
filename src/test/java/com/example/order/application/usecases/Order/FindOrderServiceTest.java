package com.example.order.application.usecases.Order;

import com.example.order.application.ports.out.OrderRepositoryPort;
import com.example.order.domain.entities.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindOrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private FindOrderService findOrderService;

    @Test
    @DisplayName("Deve retornar um Optional contendo o pedido quando o ID existir")
    void deveRetornarPedidoQuandoExistir() {
        // Arrange
        Long orderId = 1L;
        Order orderMock = new Order();
        orderMock.setId(orderId);
        orderMock.setDetails("Pedido Teste");

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(orderMock));

        // Act
        Optional<Order> resultado = findOrderService.execute(orderId);

        // Assert
        assertThat(resultado).isPresent(); // Garante que não está vazio
        assertThat(resultado.get().getId()).isEqualTo(orderId);
        assertThat(resultado.get().getDetails()).isEqualTo("Pedido Teste");

        verify(orderRepositoryPort, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando o ID não for encontrado")
    void deveRetornarVazioQuandoNaoExistir() {
        // Arrange
        Long orderId = 99L;
        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.empty());

        // Act
        Optional<Order> resultado = findOrderService.execute(orderId);

        // Assert
        assertThat(resultado).isEmpty(); // Garante que retornou Optional.empty()
        verify(orderRepositoryPort, times(1)).findById(orderId);
    }
}