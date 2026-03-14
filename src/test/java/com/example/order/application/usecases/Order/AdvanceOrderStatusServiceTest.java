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
class AdvanceOrderStatusServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private AdvanceOrderStatusService advanceOrderStatusService;

    @Test
    @DisplayName("Deve avançar status de PREPARO para PRONTO com sucesso")
    void deveAvancarDePreparoParaPronto() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setStep("PREPARO");

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        // Simula o save retornando o próprio objeto modificado
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order resultado = advanceOrderStatusService.execute(orderId);

        // Assert
        assertThat(resultado.getStep()).isEqualTo("PRONTO");
        verify(orderRepositoryPort, times(1)).save(order);
    }

    @Test
    @DisplayName("Deve avançar status de PRONTO para FINALIZADO com sucesso")
    void deveAvancarDeProntoParaFinalizado() {
        // Arrange
        Long orderId = 2L;
        Order order = new Order();
        order.setId(orderId);
        order.setStep("PRONTO");

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order resultado = advanceOrderStatusService.execute(orderId);

        // Assert
        assertThat(resultado.getStep()).isEqualTo("FINALIZADO");
        verify(orderRepositoryPort, times(1)).save(order);
    }

    @Test
    @DisplayName("Deve lançar erro IllegalArgumentException se tentar avançar RECEBIDO (Aguardando pagamento)")
    void naoDeveAvancarSeRecebido() {
        // Arrange
        Long orderId = 3L;
        Order order = new Order();
        order.setId(orderId);
        order.setStep("RECEBIDO");

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            advanceOrderStatusService.execute(orderId);
        });

        assertThat(exception.getMessage()).contains("Aguardando pagamento");
        verify(orderRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro se tentar avançar status inválido (ex: FINALIZADO)")
    void naoDeveAvancarStatusInvalido() {
        // Arrange
        Long orderId = 4L;
        Order order = new Order();
        order.setId(orderId);
        order.setStep("FINALIZADO"); // Já acabou, não deve avançar mais

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            advanceOrderStatusService.execute(orderId);
        });

        assertThat(exception.getMessage()).contains("Cannot advance status from: FINALIZADO");
        verify(orderRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar OrderNotFoundException se o pedido não existir")
    void deveLancarErroSePedidoNaoEncontrado() {
        // Arrange
        Long idInexistente = 99L;
        when(orderRepositoryPort.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            advanceOrderStatusService.execute(idInexistente);
        });

        verify(orderRepositoryPort, never()).save(any());
    }
}