package com.example.order.infrastructure.adapters.sqs;

import com.example.order.application.ports.out.EmailPort;
import com.example.order.application.usecases.Order.AtualizarStatusUseCase;
import com.example.order.application.usecases.Order.GetCustomerByCpfUseCase;
import com.example.order.domain.entities.Customer;
import com.example.order.domain.entities.VideoMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqsVideoResultListenerTest {

    @Mock
    private AtualizarStatusUseCase atualizarStatusUseCase;

    @Mock
    private GetCustomerByCpfUseCase getCustomerByCpfUseCase;

    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private SqsVideoResultListener listener;

    @Captor
    private ArgumentCaptor<String> emailBodyCaptor;

    @Test
    @DisplayName("Deve apenas atualizar o banco se o status for de sucesso (DONE)")
    void shouldOnlyUpdateDatabaseOnSuccess() {
        // Arrange
        VideoMetadata event = new VideoMetadata(
                "pedido-123", "user-456", "video.mp4", "DONE", "s3://url", LocalDateTime.now()
        );

        // Act
        listener.onResultMessage(event);

        // Assert
        // Verifica se atualizou o banco
        verify(atualizarStatusUseCase, times(1)).execute("pedido-123", "DONE", "s3://url");

        // Garante que NUNCA buscou cliente nem mandou e-mail
        verify(getCustomerByCpfUseCase, never()).execute(anyString());
        verify(emailPort, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve atualizar o banco, buscar cliente e enviar e-mail caso o status seja ERROR")
    void shouldSendEmailOnErrorStatus() {
        // Arrange
        VideoMetadata event = new VideoMetadata(
                "pedido-123", "cpf-000", "meu_casamento.mp4", "ERROR", null, LocalDateTime.now()
        );

        Customer mockCustomer = new Customer("id", "Maria da Silva", "maria@email.com", "cpf-000", "senha");

        // Simula que achou o cliente
        when(getCustomerByCpfUseCase.execute("cpf-000")).thenReturn(Optional.of(mockCustomer));

        // Act
        listener.onResultMessage(event);

        // Assert
        // Verifica se atualizou o banco com o status de erro
        verify(atualizarStatusUseCase, times(1)).execute("pedido-123", "ERROR", null);

        // Verifica se buscou o cliente
        verify(getCustomerByCpfUseCase, times(1)).execute("cpf-000");

        // Verifica o envio de e-mail e captura o corpo da mensagem
        verify(emailPort, times(1)).sendEmail(eq("maria@email.com"), eq("Erro no Processamento do seu Vídeo"), emailBodyCaptor.capture());

        // Valida se a formatação do texto substituiu os dados corretamente
        String bodyEnviado = emailBodyCaptor.getValue();
        assertTrue(bodyEnviado.contains("Olá Maria da Silva"), "O corpo do e-mail deve conter o nome do cliente");
        assertTrue(bodyEnviado.contains("meu_casamento.mp4"), "O corpo do e-mail deve conter o nome do arquivo");
        assertTrue(bodyEnviado.contains("ID do Pedido: pedido-123"), "O corpo do e-mail deve conter o ID do pedido");
    }

    @Test
    @DisplayName("Deve atualizar o banco, mas NÃO enviar e-mail se o cliente não for encontrado na API (status FAIL)")
    void shouldNotSendEmailIfCustomerNotFound() {
        // Arrange
        VideoMetadata event = new VideoMetadata(
                "pedido-123", "cpf-000", "video.mp4", "FAIL", null, LocalDateTime.now()
        );

        // Simula que a API de clientes retornou vazio
        when(getCustomerByCpfUseCase.execute("cpf-000")).thenReturn(Optional.empty());

        // Act
        listener.onResultMessage(event);

        // Assert
        verify(atualizarStatusUseCase, times(1)).execute("pedido-123", "FAIL", null);
        verify(getCustomerByCpfUseCase, times(1)).execute("cpf-000");

        // Opcional is empty, então NUNCA deve tentar mandar e-mail
        verify(emailPort, never()).sendEmail(anyString(), anyString(), anyString());
    }
}