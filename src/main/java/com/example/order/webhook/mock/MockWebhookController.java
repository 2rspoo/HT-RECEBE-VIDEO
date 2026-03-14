package com.example.order.webhook.mock;

import com.example.order.shared.exceptions.OrderNotFoundException;
import com.example.order.webhook.PaymentWebhookRequestDTO;
import com.example.order.webhook.ProcessPaymentWebhookUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;

@RestController
@RequestMapping("/mock")
@CrossOrigin(origins = "null")
public class MockWebhookController {

    private final ProcessPaymentWebhookUseCase processPaymentWebhookUseCase;
    private final Random random = new Random(); // Instância para gerar números aleatórios


    public MockWebhookController( ProcessPaymentWebhookUseCase processPaymentWebhookUseCase) {

        this.processPaymentWebhookUseCase = processPaymentWebhookUseCase;

    }

    @PostMapping

    @Operation(description = "Webhook para receber confirmação de pagamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificação de pagamento processada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado para a notificação")
    })
    public ResponseEntity<Void> receivePaymentWebhook(@RequestBody PaymentWebhookRequestDTO data) {
        try {
            int randomNumber = random.nextInt(100);
            if (randomNumber < 50) { // 50% de chance

                data.setPaymentStatus("APROVADO");
            }  else { // 50% de chance

                data.setPaymentStatus("RECUSADO");

            }





            processPaymentWebhookUseCase.execute(data.getOrderId(), data.getPaymentStatus());
            return ResponseEntity.ok().build(); // Retorna 200 OK para o serviço de pagamento
        } catch (OrderNotFoundException e) {
            // Em caso de erro, é uma boa prática notificar o serviço de pagamento com um 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
