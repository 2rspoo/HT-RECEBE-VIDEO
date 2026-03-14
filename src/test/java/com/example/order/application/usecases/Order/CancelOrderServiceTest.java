package com.example.order.application.usecases.Order;

import com.example.order.application.ports.out.OrderRepositoryPort;
import com.example.order.domain.entities.Order;
import com.example.order.shared.exceptions.OrderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelOrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private CancelOrderService cancelOrderService;

    @Test
    @DisplayName("Deve cancelar um pedido com sucesso (mudando status para CANCELADO)")
    void deveCancelarPedidoComSucesso() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStep("RECEBIDO"); // Status inicial válido para cancelamento

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        // Simula o save retornando o próprio objeto
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Order resultado = cancelOrderService.execute(orderId);

        // Assert
        assertThat(resultado.getStep()).isEqualTo("CANCELADO");
        verify(orderRepositoryPort, times(1)).save(order);
    }

    @Test
    @DisplayName("Deve lançar erro OrderNotFoundException se o pedido não existir")
    void deveLancarErroSePedidoNaoEncontrado() {
        // Arrange
        Long idInexistente = 99L;
        when(orderRepositoryPort.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            cancelOrderService.execute(idInexistente);
        });

        // Garante que não tentou salvar nada
        verify(orderRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro IllegalArgumentException se o pedido JÁ estiver cancelado")
    void deveLancarErroSeJaEstiverCancelado() {
        // Arrange
        Long orderId = 2L;
        Order order = new Order();
        order.setId(orderId);
        order.setStep("CANCELADO"); // Já está cancelado

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cancelOrderService.execute(orderId);
        });

        assertThat(exception.getMessage()).isEqualTo("Pedido Já Cancelado.");

        // Garante que não chamou o save novamente
        verify(orderRepositoryPort, never()).save(any());
    }
}