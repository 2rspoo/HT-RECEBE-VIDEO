package com.example.order.application.usecases.Order;

import com.example.order.application.ports.out.OrderRepositoryPort;
import com.example.order.domain.entities.Order;
import com.example.order.webhook.MercadoPagoPaymentResponseDTO;
import com.example.order.webhook.PaymentServicePort;
import com.example.order.webhook.ProcessPaymentWebhookUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @Mock
    private PaymentServicePort paymentServicePort;

    @Mock
    private ProcessPaymentWebhookUseCase processPaymentWebhookUseCase;

    @InjectMocks
    private CreateOrderService createOrderService;

    @Test
    @DisplayName("Deve salvar pedido, gerar QR Code e processar webhook inicial com sucesso")
    void deveExecutarFluxoDeCriacaoCompleto() {
        // Arrange
        Order orderEntrada = new Order();
        orderEntrada.setPrice(100);
        orderEntrada.setDetails("Hamburguer");

        // Simulamos que o banco devolve o pedido agora com ID preenchido
        Order orderSalva = new Order();
        orderSalva.setId(123L);
        orderSalva.setPrice(100);
        orderSalva.setDetails("Hamburguer");

        // Resposta simulada do Mercado Pago
        MercadoPagoPaymentResponseDTO responsePagamento = new MercadoPagoPaymentResponseDTO();
        responsePagamento.setStatus("PENDENTE");
        responsePagamento.setQrData("qrcode-123");

        // Configurando os Mocks
        when(orderRepositoryPort.save(orderEntrada)).thenReturn(orderSalva);
        when(paymentServicePort.createQrCodePayment(orderSalva)).thenReturn(responsePagamento);

        // Act
        MercadoPagoPaymentResponseDTO resultado = createOrderService.execute(orderEntrada);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getQrData()).isEqualTo("qrcode-123");

        // --- Verificação de Ordem e Fluxo (Crucial para UseCases) ---

        // 1. Garante que salvou no banco
        verify(orderRepositoryPort, times(1)).save(orderEntrada);

        // 2. Garante que chamou o pagamento com o pedido SALVO (que tem ID)
        verify(paymentServicePort, times(1)).createQrCodePayment(orderSalva);

        // 3. Garante que atualizou o status inicial (Webhook UseCase)
        verify(processPaymentWebhookUseCase, times(1)).execute(123L, "PENDENTE");
    }

    @Test
    @DisplayName("Deve lançar exceção se falhar ao salvar no repositório")
    void deveFalharSeRepositorioFalhar() {
        // Arrange
        Order order = new Order();
        when(orderRepositoryPort.save(order)).thenThrow(new RuntimeException("Erro banco"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> createOrderService.execute(order));

        // Garante que se falhar no banco, nem tenta chamar o pagamento
        verifyNoInteractions(paymentServicePort);
        verifyNoInteractions(processPaymentWebhookUseCase);
    }
}