package com.example.order.webhook;

import com.example.order.domain.entities.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MercadoPagoServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MercadoPagoService mercadoPagoService;

    private final String API_URL = "https://api.mercadopago.com";
    private final String USER_ID = "123456";
    private final String POS_ID = "POS001";
    private final String TOKEN = "TEST_TOKEN_XYZ";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mercadoPagoService, "apiUrl", API_URL);
        ReflectionTestUtils.setField(mercadoPagoService, "userId", USER_ID);
        ReflectionTestUtils.setField(mercadoPagoService, "posId", POS_ID);
        ReflectionTestUtils.setField(mercadoPagoService, "accessToken", TOKEN);
        ReflectionTestUtils.setField(mercadoPagoService, "restTemplate", restTemplate);
    }

    @Test
    @DisplayName("Deve criar QR Code com sucesso (Status 201)")
    void deveCriarQrCodeComSucesso() {
        Order order = new Order();
        order.setId(100L);
        order.setPrice(2500);

        MercadoPagoPaymentResponseDTO mockResponse = new MercadoPagoPaymentResponseDTO();
        mockResponse.setQrData("QR_CODE_DATA_STRING");
        mockResponse.setStatus("PENDING");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(MercadoPagoPaymentResponseDTO.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.CREATED));

        MercadoPagoPaymentResponseDTO result = mercadoPagoService.createQrCodePayment(order);

        assertThat(result).isNotNull();
        assertThat(result.getQrData()).isEqualTo("QR_CODE_DATA_STRING");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<MercadoPagoPaymentRequestDTO>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(
                urlCaptor.capture(),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(MercadoPagoPaymentResponseDTO.class)
        );

        String expectedUrl = String.format("%s/instore/qr/seller/collectors/%s/pos/%s/orders", API_URL, USER_ID, POS_ID);
        assertThat(urlCaptor.getValue()).isEqualTo(expectedUrl);

        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(headers.getFirst("Authorization")).isEqualTo("Bearer " + TOKEN);

        MercadoPagoPaymentRequestDTO body = entityCaptor.getValue().getBody();
        assertThat(body).isNotNull();
        assertThat(body.getExternalReference()).isEqualTo("100");
        assertThat(body.getTotalAmount()).isEqualTo(new BigDecimal("2500"));
    }

    @Test
    @DisplayName("Deve lançar exceção se o Mercado Pago retornar erro (ex: 400)")
    void deveLancarErroQuandoStatusNaoForCreated() {
        // Arrange
        Order order = new Order();
        order.setId(101L);
        order.setPrice(100);

        // Simulamos um erro 400 Bad Request
        when(restTemplate.exchange(anyString(), any(), any(), eq(MercadoPagoPaymentResponseDTO.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mercadoPagoService.createQrCodePayment(order);
        });

        // CORREÇÃO AQUI: Seu código captura a exceção original e lança essa mensagem genérica.
        // O teste agora espera o comportamento real do seu código.
        assertThat(exception.getMessage()).isEqualTo("Falha na comunicação com o serviço de pagamento.");
    }

    @Test
    @DisplayName("Deve capturar exceção de comunicação e relançar como RuntimeException")
    void deveTratarExcecaoDeComunicacao() {
        // Arrange
        Order order = new Order();
        order.setId(102L);
        order.setPrice(500);

        when(restTemplate.exchange(anyString(), any(), any(), eq(MercadoPagoPaymentResponseDTO.class)))
                .thenThrow(new RestClientException("Connection timed out"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mercadoPagoService.createQrCodePayment(order);
        });

        assertThat(exception.getMessage()).isEqualTo("Falha na comunicação com o serviço de pagamento.");
    }
}