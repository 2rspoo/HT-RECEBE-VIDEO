// src/main/java/com/example/order/infrastructure/adapters/in/web/OrderController.java
package com.example.order.webhook;

import com.example.order.shared.exceptions.OrderNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/webhook/payment")
@CrossOrigin(origins = "null")
public class PaymentWebhookController {


    private final ProcessPaymentWebhookUseCase processPaymentWebhookUseCase;



    public PaymentWebhookController( ProcessPaymentWebhookUseCase processPaymentWebhookUseCase) {

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
            processPaymentWebhookUseCase.execute(data.getOrderId(), data.getPaymentStatus());
            return ResponseEntity.ok().build(); // Retorna 200 OK para o serviço de pagamento
        } catch (OrderNotFoundException e) {
            // Em caso de erro, é uma boa prática notificar o serviço de pagamento com um 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}