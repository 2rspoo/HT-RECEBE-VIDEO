package com.example.order;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.example.order.application.ports.in.Order.*;
import com.example.order.infrastructure.adapters.out.persistence.JpaAdapter.OrderDynamoAdapter;
import com.example.order.webhook.PaymentServicePort; // Importe a interface de pagamento
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class OrderApplicationTests {

	// --- MOCKS DE INFRAESTRUTURA (BANCO) ---
	@MockitoBean
	private OrderDynamoAdapter orderDynamoAdapter;

	@MockitoBean
	private AmazonDynamoDB amazonDynamoDB;

	@MockitoBean
	private DynamoDBMapper dynamoDBMapper;

	// --- MOCK DA PORTA DE PAGAMENTO (A SOLUÇÃO DO ERRO) ---
	// Ao mockar a interface, o Spring substitui qualquer implementação (Real ou Mock)
	// por este objeto vazio. Isso evita que o 'MercadoPagoService' real tente nascer
	// e reclame da falta de 'mercadopago.pos.id'.
	@MockitoBean
	private PaymentServicePort paymentServicePort;

	// --- MOCKS DE USECASES (Para o Controller não falhar) ---
	@MockitoBean private GetAllOrdersUseCase getAllOrdersUseCase;
	@MockitoBean private GetOrdersByStepUseCase getOrdersByStepUseCase;
	@MockitoBean private CreateOrderUseCase createOrderUseCase;
	@MockitoBean private AdvanceOrderStatusUseCase advanceOrderStatusUseCase;
	@MockitoBean private CancelOrderUseCase cancelOrderUseCase;
	@MockitoBean private FindOrderUseCase findOrderUseCase;

	@Test
	void contextLoads() {
		// Agora o Spring deve subir o contexto sem erros,
		// pois todas as dependências externas e complexas são Mocks.
	}
}