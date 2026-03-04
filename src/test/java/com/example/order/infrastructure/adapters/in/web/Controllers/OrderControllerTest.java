package com.example.order.infrastructure.adapters.in.web.Controllers;

import com.example.order.application.ports.in.Order.*;
import com.example.order.domain.entities.Order;
import com.example.order.infrastructure.adapters.in.web.Dtos.OrderRequestDTO;
import com.example.order.shared.exceptions.OrderNotFoundException;
import com.example.order.webhook.MercadoPagoPaymentResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mocks dos 6 UseCases injetados no Controller ---
    @MockitoBean private GetAllOrdersUseCase getAllOrdersUseCase;
    @MockitoBean private GetOrdersByStepUseCase getOrdersByStepUseCase;
    @MockitoBean private CreateOrderUseCase createOrderUseCase;
    @MockitoBean private AdvanceOrderStatusUseCase advanceOrderStatusUseCase;
    @MockitoBean private CancelOrderUseCase cancelOrderUseCase;
    @MockitoBean private FindOrderUseCase findOrderUseCase;

    // --- Helper para criar um pedido padrão ---
    private Order criarOrderMock(Long id, String step, String paymentStatus) {
        return new Order(id, 1L, step, LocalDate.now(), LocalTime.now(), 5000, "Detalhes", paymentStatus);
    }

    @Test
    @DisplayName("GET /order - Deve retornar lista de pedidos")
    void deveListarTodosPedidos() throws Exception {
        List<Order> orders = Arrays.asList(
                criarOrderMock(1L, "RECEBIDO", "PENDENTE"),
                criarOrderMock(2L, "PRONTO", "APROVADO")
        );

        when(getAllOrdersUseCase.execute()).thenReturn(orders);

        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].step").value("RECEBIDO"));
    }

    @Test
    @DisplayName("GET /order/step/{step} - Deve filtrar pedidos por status")
    void deveListarPedidosPorStep() throws Exception {
        String step = "PRONTO";
        List<Order> orders = Collections.singletonList(criarOrderMock(2L, step, "APROVADO"));

        when(getOrdersByStepUseCase.execute(step)).thenReturn(orders);

        mockMvc.perform(get("/order/step/{step}", step))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].step").value(step));
    }

    @Test
    @DisplayName("POST /order - Sucesso (Retorna 201 e dados do Mercado Pago)")
    void deveCriarPedidoComSucesso() throws Exception {
        // Arrange
        // --- CORREÇÃO AQUI: Usando construtor vazio + Setters ---
        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setIdcustomer(1L);
        requestDTO.setPrice(5000);
        requestDTO.setDetails("Sem sal");
        // --------------------------------------------------------

        MercadoPagoPaymentResponseDTO mpResponse = new MercadoPagoPaymentResponseDTO();

        when(createOrderUseCase.execute(any(Order.class))).thenReturn(mpResponse);

        // Act & Assert
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /order - Falha na Integração (Retorna 202 e Objeto Order)")
    void deveRetornar202QuandoIntegracaoFalhar() throws Exception {
        // Arrange
        // --- CORREÇÃO: Usando construtor vazio e setters ---
        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setIdcustomer(1L);
        requestDTO.setPrice(5000);
        requestDTO.setDetails("Sem sal");
        // ---------------------------------------------------

        // Simulando erro na integração (o controller captura RuntimeException)
        when(createOrderUseCase.execute(any(Order.class)))
                .thenThrow(new RuntimeException("Mercado Pago indisponível"));

        // Act & Assert
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isAccepted()) // 202 (Lógica do seu catch)
                .andExpect(jsonPath("$.idcustomer").value(1)); // Retorna o objeto newOrder
    }

    @Test
    @DisplayName("PUT /order/next/{id} - Deve avançar status com sucesso")
    void deveAvancarStatus() throws Exception {
        Long id = 1L;
        Order orderAtualizada = criarOrderMock(id, "EM_PREPARO", "APROVADO");

        when(advanceOrderStatusUseCase.execute(id)).thenReturn(orderAtualizada);

        mockMvc.perform(put("/order/next/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.step").value("EM_PREPARO"));
    }

    @Test
    @DisplayName("PUT /order/next/{id} - Deve retornar 404 se pedido não existir")
    void deveRetornar404AoAvancarSeNaoExistir() throws Exception {
        Long id = 99L;
        when(advanceOrderStatusUseCase.execute(id))
                .thenThrow(new OrderNotFoundException("Pedido não encontrado"));

        mockMvc.perform(put("/order/next/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /order/{id} - Deve cancelar pedido com sucesso")
    void deveCancelarPedido() throws Exception {
        Long id = 1L;
        Order orderCancelada = criarOrderMock(id, "CANCELADO", "RECUSADO");

        when(cancelOrderUseCase.execute(id)).thenReturn(orderCancelada);

        mockMvc.perform(delete("/order/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.step").value("CANCELADO"));
    }

    @Test
    @DisplayName("GET /order/pagamento/{id} - Deve retornar status do pagamento")
    void deveConsultarPagamento() throws Exception {
        Long id = 1L;
        Order order = criarOrderMock(id, "RECEBIDO", "APROVADO");

        when(findOrderUseCase.execute(id)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/order/pagamento/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("APROVADO"));
    }

    @Test
    @DisplayName("GET /order/pagamento/{id} - Deve retornar 404 se pedido não existir")
    void deveRetornar404PagamentoSePedidoNaoExistir() throws Exception {
        Long id = 99L;
        when(findOrderUseCase.execute(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/order/pagamento/{id}", id))
                .andExpect(status().isNotFound());
    }
}