package com.example.order.infrastructure.adapters.sqs;


import com.example.order.application.usecases.Order.AtualizarStatusUseCase;
import com.example.order.application.ports.out.EmailPort;
import com.example.order.application.usecases.Order.GetCustomerByCpfUseCase;
import com.example.order.domain.entities.VideoMetadata;
import com.example.order.domain.entities.Customer;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SqsVideoResultListener {

    private final AtualizarStatusUseCase atualizarStatusUseCase;
    private final GetCustomerByCpfUseCase getCustomerByCpfUseCase;
    private final EmailPort emailPort;

    public SqsVideoResultListener(
            AtualizarStatusUseCase atualizarStatusUseCase,
            GetCustomerByCpfUseCase getCustomerByCpfUseCase,
            EmailPort emailPort) {
        this.atualizarStatusUseCase = atualizarStatusUseCase;
        this.getCustomerByCpfUseCase = getCustomerByCpfUseCase;
        this.emailPort = emailPort;
    }

    @SqsListener("${AWS_SQS_RESULT_URL}")
    public void onResultMessage(VideoMetadata event) {
        System.out.println("<<< Recebido resultado do processamento para o ID: " + event.pedidoId());

        // 1. Atualiza o banco de dados local (DynamoDB)
        atualizarStatusUseCase.execute(
                event.pedidoId(),
                event.status(),
                event.s3Url()
        );

        // 2. Se houver erro, busca o cliente via HTTP e envia e-mail
        if ("ERROR".equalsIgnoreCase(event.status()) || "FAIL".equalsIgnoreCase(event.status())) {
            enviarNotificacaoErro(event);
        }
    }

    private void enviarNotificacaoErro(VideoMetadata event) {
        // Vai disparar a requisição HTTP pelo RestTemplate no CustomerApiClientAdapter
        getCustomerByCpfUseCase.execute(event.userId()).ifPresent(customer -> {
            String subject = "Erro no Processamento do seu Vídeo";
            String body = String.format(
                    "Olá %s,\n\nLamentamos informar que ocorreu um erro ao processar o seu vídeo: %s.\n" +
                            "ID do Pedido: %s\n\nPor favor, tente realizar o upload novamente mais tarde.",
                    customer.name(), event.fileName(), event.pedidoId()
            );

            emailPort.sendEmail(customer.email(), subject, body);
        });
    }
}
