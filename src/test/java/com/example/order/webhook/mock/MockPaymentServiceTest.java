package com.example.order.webhook.mock;

import com.example.order.domain.entities.Order;
import com.example.order.webhook.MercadoPagoPaymentResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MockPaymentServiceTest {

    @Test
    @DisplayName("Deve retornar resposta simulada (Mock) corretamente com ID do pedido")
    void deveRetornarMockCorretamente() {
        // Arrange
        MockPaymentService mockService = new MockPaymentService();

        Order order = new Order();
        order.setId(999L); // ID para validar a concatenação

        // Act
        MercadoPagoPaymentResponseDTO response = mockService.createQrCodePayment(order);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("PENDENTE");
        assertThat(response.getMessage()).isEqualTo("Pagamento criado");

        // Verifica se a lógica de concatenar o ID no QR Data funcionou
        assertThat(response.getQrData()).isEqualTo("MOCK_QR_DATA_PENDENTE   999");
    }
}