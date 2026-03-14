package com.example.order.infrastructure.adapters.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SesException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AwsSesEmailAdapterTest {

    @Mock
    private SesClient sesClient;

    @InjectMocks
    private AwsSesEmailAdapter adapter;

    @Captor
    private ArgumentCaptor<SendEmailRequest> requestCaptor;

    private final String senderEmail = "no-reply@meusistema.com";

    @BeforeEach
    void setUp() {
        // Como o senderEmail é injetado via @Value no Spring,
        // usamos o ReflectionTestUtils para setar esse valor no teste unitário.
        ReflectionTestUtils.setField(adapter, "senderEmail", senderEmail);
    }

    @Test
    @DisplayName("Deve construir o request corretamente e enviar o e-mail via AWS SES")
    void shouldSendEmailSuccessfully() {
        // Arrange
        String to = "cliente@exemplo.com";
        String subject = "Erro no Processamento";
        String body = "Ocorreu um erro ao processar seu vídeo.";

        // Act
        adapter.sendEmail(to, subject, body);

        // Assert
        // Verifica se o SesClient foi chamado e captura o request montado
        verify(sesClient, times(1)).sendEmail(requestCaptor.capture());
        SendEmailRequest capturedRequest = requestCaptor.getValue();

        // Valida se o "De", "Para", "Assunto" e "Corpo" foram mapeados corretamente
        assertEquals(senderEmail, capturedRequest.source(), "O remetente deve ser o configurado no @Value");
        assertEquals(to, capturedRequest.destination().toAddresses().get(0), "O destinatário deve ser mapeado corretamente");
        assertEquals(subject, capturedRequest.message().subject().data(), "O assunto deve ser mapeado corretamente");
        assertEquals(body, capturedRequest.message().body().text().data(), "O corpo do e-mail deve ser mapeado corretamente");
    }

    @Test
    @DisplayName("Não deve estourar exceção se o SESClient falhar (deve apenas logar)")
    void shouldNotThrowExceptionWhenSesClientFails() {
        // Arrange
        String to = "cliente@exemplo.com";
        String subject = "Teste";
        String body = "Corpo";

        // Simula a AWS lançando um erro (ex: credenciais inválidas ou e-mail não verificado)
        doThrow(SesException.builder().message("E-mail não verificado").build())
                .when(sesClient).sendEmail(any(SendEmailRequest.class));

        // Act & Assert
        // Garante que o método não lança a exceção para quem chamou (o try/catch funcionou)
        assertDoesNotThrow(() -> {
            adapter.sendEmail(to, subject, body);
        }, "A exceção deveria ter sido capturada pelo try/catch interno");

        // Garante que tentou enviar
        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
    }
}