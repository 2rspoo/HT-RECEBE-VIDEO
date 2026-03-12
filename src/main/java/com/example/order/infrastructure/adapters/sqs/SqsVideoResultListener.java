package com.example.order.infrastructure.adapters.sqs;


import com.example.order.application.usecases.Order.AtualizarStatusUseCase;
import com.example.order.domain.entities.VideoMetadata;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SqsVideoResultListener {

    private final AtualizarStatusUseCase atualizarStatusUseCase;

    public SqsVideoResultListener(AtualizarStatusUseCase atualizarStatusUseCase) {
        this.atualizarStatusUseCase = atualizarStatusUseCase;
    }

    // Escuta a fila de respostas.
    @SqsListener("${AWS_SQS_RESULT_URL}")
    public void onResultMessage(VideoMetadata event) {
        System.out.println("<<< Recebido resultado do processamento para o ID: " + event.pedidoId());

        // Chama o UseCase para atualizar o DynamoDB
        atualizarStatusUseCase.execute(
                event.pedidoId(),
                event.status(),
                event.s3Url().getBytes() // Que agora contém o caminho do arquivo processado
        );
    }
}
