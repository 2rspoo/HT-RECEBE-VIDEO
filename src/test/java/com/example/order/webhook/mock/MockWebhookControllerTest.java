package com.example.order.webhook.mock;

import com.example.order.shared.exceptions.OrderNotFoundException;
import com.example.order.webhook.PaymentWebhookRequestDTO;
import com.example.order.webhook.ProcessPaymentWebhookUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MockWebhookController.class)
class MockWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProcessPaymentWebhookUseCase processPaymentWebhookUseCase;

    @Test
    @DisplayName("Deve processar webhook e definir status aleatório (APROVADO ou RECUSADO)")
    void deveProcessarWebhookComSucesso() throws Exception {
        // Arrange
        Long orderId = 123L;
        PaymentWebhookRequestDTO requestDTO = new PaymentWebhookRequestDTO();
        requestDTO.setOrderId(orderId);
        // Não definimos status aqui, pois o controller define aleatoriamente

        // Act
        mockMvc.perform(post("/mock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        // Assert
        // Como o resultado é aleatório, usamos um CAPTOR para pegar o valor que foi passado
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);

        // Verificamos se o método execute foi chamado com o ID correto e capturamos o status
        verify(processPaymentWebhookUseCase).execute(eq(orderId), statusCaptor.capture());

        String statusGerado = statusCaptor.getValue();

        // O teste passa se o status for "APROVADO" OU "RECUSADO"
        assertThat(statusGerado).isIn("APROVADO", "RECUSADO");
    }

    @Test
    @DisplayName("Deve retornar 404 se o pedido não for encontrado ao processar")
    void deveRetornar404QuandoPedidoNaoExiste() throws Exception {
        // Arrange
        Long orderId = 999L;
        PaymentWebhookRequestDTO requestDTO = new PaymentWebhookRequestDTO();
        requestDTO.setOrderId(orderId);

        // Simulamos que qualquer chamada ao useCase com esse ID lança a exceção,
        // independentemente do status aleatório gerado (usamos anyString())
        doThrow(new OrderNotFoundException("Pedido não encontrado"))
                .when(processPaymentWebhookUseCase).execute(eq(orderId), anyString());

        // Act & Assert
        mockMvc.perform(post("/mock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound()); // 404
    }
}