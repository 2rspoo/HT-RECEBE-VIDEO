package com.example.order.webhook;

import com.example.order.domain.entities.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;

@Service
@Profile("prod") // <-- Ativada apenas quando o perfil 'prod' está ativo
public class MercadoPagoService implements PaymentServicePort {

    @Value("${mercadopago.access.token:dummy_token}")
    private String accessToken;
    @Value("${mercadopago.user.id}")
    private String userId;
    @Value("${mercadopago.pos.id}")
    private String posId;
    @Value("${mercadopago.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public MercadoPagoPaymentResponseDTO createQrCodePayment(Order order) {
        System.out.println("--- Usando o serviço REAL do Mercado Pago ---");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        // Lógica para chamar a API e retornar o DTO de resposta
        // ... (código que você já possui)
        MercadoPagoPaymentRequestDTO requestBody = new MercadoPagoPaymentRequestDTO();
        requestBody.setExternalReference(String.valueOf(order.getId()));
        requestBody.setTotalAmount(new BigDecimal(order.getPrice()));
        requestBody.setDescription("Pedido de ID: " + order.getId());
        requestBody.setItems(Collections.emptyList());

        HttpEntity<MercadoPagoPaymentRequestDTO> entity = new HttpEntity<>(requestBody, headers);

        String url = String.format("%s/instore/qr/seller/collectors/%s/pos/%s/orders", apiUrl, userId, posId);

        try {
            ResponseEntity<MercadoPagoPaymentResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, MercadoPagoPaymentResponseDTO.class
            );
            if (response.getStatusCode() == HttpStatus.CREATED) {
                return response.getBody();
            } else {
                throw new RuntimeException("Erro ao gerar QR Code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Erro na integração com o Mercado Pago: " + e.getMessage());
            throw new RuntimeException("Falha na comunicação com o serviço de pagamento.");
        }
    }
}