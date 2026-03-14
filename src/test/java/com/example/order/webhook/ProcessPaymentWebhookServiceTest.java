package com.example.order.webhook;

import com.example.order.application.ports.out.OrderRepositoryPort;
import com.example.order.domain.entities.Order;
import com.example.order.shared.exceptions.OrderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentWebhookServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private ProcessPaymentWebhookService processPaymentWebhookService;

    // Método auxiliar para criar um pedido padrão para os testes
    private Order criarPedidoInicial() {
        Order order = new Order();
        order.setId(1L);
        order.setStep("RECEBIDO"); // Status inicial padrão
        order.setPaymentStatus("PENDENTE");
        return order;
    }

    @Test
    @DisplayName("Deve atualizar para PREPARO quando pagamento for APROVADO")
    void deveAvancarStatusSeAprovado() {
        // Arrange
        Long orderId = 1L;
        Order order = criarPedidoInicial();

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        processPaymentWebhookService.execute(orderId, "APROVADO");

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepositoryPort).save(orderCaptor.capture());

        Order orderSalva = orderCaptor.getValue();
        assertThat(orderSalva.getPaymentStatus()).isEqualTo("APROVADO");
        assertThat(orderSalva.getStep()).isEqualTo("PREPARO"); // Regra de negócio validada
    }

    @Test
    @DisplayName("Deve atualizar para CANCELADO quando pagamento for RECUSADO")
    void deveCancelarStatusSeRecusado() {
        // Arrange
        Long orderId = 1L;
        Order order = criarPedidoInicial();

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        processPaymentWebhookService.execute(orderId, "RECUSADO");

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepositoryPort).save(orderCaptor.capture());

        Order orderSalva = orderCaptor.getValue();
        assertThat(orderSalva.getPaymentStatus()).isEqualTo("RECUSADO");
        assertThat(orderSalva.getStep()).isEqualTo("CANCELADO"); // Regra de negócio validada
    }

    @Test
    @DisplayName("Deve apenas atualizar status do pagamento se for PENDENTE (sem mudar Step)")
    void naoDeveMudarStepSePendente() {
        // Arrange
        Long orderId = 1L;
        Order order = criarPedidoInicial();
        order.setStep("RECEBIDO"); // Garante estado inicial

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        processPaymentWebhookService.execute(orderId, "PENDENTE");

        // Assert
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepositoryPort).save(orderCaptor.capture());

        Order orderSalva = orderCaptor.getValue();
        assertThat(orderSalva.getPaymentStatus()).isEqualTo("PENDENTE");
        assertThat(orderSalva.getStep()).isEqualTo("RECEBIDO"); // Step deve permanecer inalterado
    }

    @Test
    @DisplayName("Deve lançar exceção OrderNotFoundException se ID não existir")
    void deveLancarErroSePedidoNaoExiste() {
        // Arrange
        Long orderIdInexistente = 999L;
        when(orderRepositoryPort.findById(orderIdInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () ->
                processPaymentWebhookService.execute(orderIdInexistente, "APROVADO")
        );

        // Garante que o save NUNCA foi chamado
        verify(orderRepositoryPort, never()).save(any());
    }
}