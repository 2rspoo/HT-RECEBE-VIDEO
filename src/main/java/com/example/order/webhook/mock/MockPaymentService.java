package com.example.order.webhook.mock;

import com.example.order.webhook.PaymentServicePort;
import com.example.order.domain.entities.Order;
import com.example.order.webhook.MercadoPagoPaymentResponseDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;



@Service
@Profile("dev") // <-- Ativada apenas quando o perfil 'dev' está ativo
public class MockPaymentService implements PaymentServicePort {


    @Override
    public MercadoPagoPaymentResponseDTO createQrCodePayment(Order order) {

        // Simula uma resposta de sucesso da API
        MercadoPagoPaymentResponseDTO mockResponse = new MercadoPagoPaymentResponseDTO();


        mockResponse.setStatus("PENDENTE"); // O status na resposta do pagamento pode ser nulo
        mockResponse.setMessage("Pagamento criado");
        mockResponse.setQrData("MOCK_QR_DATA_PENDENTE   " + order.getId());

        return mockResponse;
    }
}