package com.example.order.infrastructure.adapters.email;

import com.example.order.application.ports.out.EmailPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Component
public class AwsSesEmailAdapter implements EmailPort {

    private final SesClient sesClient;

    // Injeta o e-mail verificado configurado no application.properties
    @Value("${aws.ses.sender}")
    private String senderEmail;

    public AwsSesEmailAdapter(SesClient sesClient) {
        this.sesClient = sesClient;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SendEmailRequest request = SendEmailRequest.builder()
                    .destination(Destination.builder().toAddresses(to).build())
                    .message(Message.builder()
                            .subject(Content.builder().data(subject).build())
                            .body(Body.builder().text(Content.builder().data(body).build()).build())
                            .build())
                    .source(senderEmail) // <-- Usando a variável do properties
                    .build();

            sesClient.sendEmail(request);
            System.out.println(">>> E-mail de erro enviado com sucesso para: " + to);
        } catch (Exception e) {
            System.err.println("Falha ao enviar e-mail: " + e.getMessage());

        }
    }
}
