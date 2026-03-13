package com.example.order.infrastructure.adapters.email;

import com.example.order.application.ports.out.EmailPort;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Component
public class AwsSesEmailAdapter implements EmailPort {

        private final SesClient sesClient;

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
                    .source("seu-email-verificado@dominio.com") // Deve ser um e-mail verificado no SES
                    .build();

            sesClient.sendEmail(request);
            System.out.println(">>> E-mail de erro enviado para: " + to);
        } catch (Exception e) {
            System.err.println("Falha ao enviar e-mail: " + e.getMessage());
        }
    }
}