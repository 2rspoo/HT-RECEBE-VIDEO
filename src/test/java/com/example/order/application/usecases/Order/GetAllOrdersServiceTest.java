package com.example.order.application.usecases.Order;

import com.example.order.application.ports.out.OrderRepositoryPort;
import com.example.order.domain.entities.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate; // Import necessário
import java.time.LocalTime; // Import necessário
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllOrdersServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private GetAllOrdersService getAllOrdersService;

    // Helper ajustado para converter String em LocalDate/LocalTime
    private Order criarPedido(Long id, String status, String data, String hora) {
        Order order = new Order();
        order.setId(id);
        order.setStep(status);

        // CORREÇÃO: Parse das Strings para os objetos de data/hora
        order.setDate(LocalDate.parse(data));
        order.setTime(LocalTime.parse(hora));

        return order;
    }

    @Test
    @DisplayName("Deve filtrar (remover) pedidos FINALIZADO e CANCELADO")
    void deveFiltrarPedidosFinalizadosECancelados() {
        // Arrange
        Order pedido1 = criarPedido(1L, "PRONTO", "2023-10-10", "10:00");
        Order pedido2 = criarPedido(2L, "FINALIZADO", "2023-10-10", "11:00");
        Order pedido3 = criarPedido(3L, "CANCELADO", "2023-10-10", "12:00");
        Order pedido4 = criarPedido(4L, "RECEBIDO", "2023-10-10", "13:00");

        when(orderRepositoryPort.findAll()).thenReturn(Arrays.asList(pedido1, pedido2, pedido3, pedido4));

        // Act
        List<Order> resultado = getAllOrdersService.execute();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Order::getStep)
                .contains("PRONTO", "RECEBIDO")
                .doesNotContain("FINALIZADO", "CANCELADO");
    }

    @Test
    @DisplayName("Deve ordenar corretamente por Status (PRONTO > PREPARO > RECEBIDO)")
    void deveOrdenarPorStatus() {
        // Arrange
        Order p1 = criarPedido(1L, "RECEBIDO", "2023-10-10", "10:00"); // Prioridade 3
        Order p2 = criarPedido(2L, "PRONTO", "2023-10-10", "10:00");   // Prioridade 1
        Order p3 = criarPedido(3L, "PREPARO", "2023-10-10", "10:00");  // Prioridade 2

        when(orderRepositoryPort.findAll()).thenReturn(Arrays.asList(p1, p2, p3));

        // Act
        List<Order> resultado = getAllOrdersService.execute();

        // Assert
        assertThat(resultado).containsExactly(p2, p3, p1);
    }

    @Test
    @DisplayName("Deve usar Data e Hora como critério de desempate para mesmo status")
    void deveDesempatarPorDataEHora() {
        // Arrange
        // Dois pedidos com mesmo status "RECEBIDO", mas horários diferentes
        Order pAntigo = criarPedido(1L, "RECEBIDO", "2023-10-10", "09:00");
        Order pNovo = criarPedido(2L, "RECEBIDO", "2023-10-10", "10:00");

        // Um pedido PRONTO
        Order pPronto = criarPedido(3L, "PRONTO", "2023-10-10", "12:00");

        when(orderRepositoryPort.findAll()).thenReturn(Arrays.asList(pNovo, pPronto, pAntigo));

        // Act
        List<Order> resultado = getAllOrdersService.execute();

        // Assert
        assertThat(resultado).containsExactly(pPronto, pAntigo, pNovo);
    }

    @Test
    @DisplayName("Deve retornar lista vazia se não houver pedidos no banco")
    void deveRetornarVazio() {
        // Arrange
        when(orderRepositoryPort.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Order> resultado = getAllOrdersService.execute();

        // Assert
        assertThat(resultado).isEmpty();
    }
}