// src/main/java/com/example/order/infrastructure/adapters/in/web/OrderController.java
package com.example.order.infrastructure.adapters.in.web.Controllers;

import com.example.order.application.ports.in.Order.FindOrderUseCase;
import com.example.order.application.ports.in.Order.*;

import com.example.order.domain.entities.*;
import com.example.order.webhook.MercadoPagoPaymentResponseDTO;
import com.example.order.infrastructure.adapters.in.web.Dtos.OrderRequestDTO;
import com.example.order.infrastructure.adapters.in.web.Dtos.OrderResponseDTO;

import com.example.order.shared.exceptions.OrderNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "null")
public class OrderController {

    private final GetAllOrdersUseCase getAllOrdersUseCase;
    private final GetOrdersByStepUseCase getOrdersByStepUseCase;
    private final CreateOrderUseCase createOrderUseCase;
    private final AdvanceOrderStatusUseCase advanceOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    private final FindOrderUseCase findOrderUseCase;


    public OrderController(GetAllOrdersUseCase getAllOrdersUseCase,
                           GetOrdersByStepUseCase getOrdersByStepUseCase,
                           CreateOrderUseCase createOrderUseCase,
                           AdvanceOrderStatusUseCase advanceOrderStatusUseCase,
                           CancelOrderUseCase cancelOrderUseCase, FindOrderUseCase findOrderUseCase) {
        this.getAllOrdersUseCase = getAllOrdersUseCase;
        this.getOrdersByStepUseCase = getOrdersByStepUseCase;
        this.createOrderUseCase = createOrderUseCase;
        this.advanceOrderStatusUseCase = advanceOrderStatusUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
                this.findOrderUseCase = findOrderUseCase;
    }

    @Operation(description = "Lista de todos os pedidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna lista de pedidos"),
            @ApiResponse(responseCode = "400", description = "Não existe pedido")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<Order> orders = getAllOrdersUseCase.execute();
        return ResponseEntity.ok(orders.stream().map(OrderResponseDTO::fromDomain).collect(Collectors.toList()));
    }

    @Operation(description = "Lista os pedidos por status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna lista de pedidos por status"),
            @ApiResponse(responseCode = "400", description = "Não existe o pedido")
    })
    @GetMapping("/step/{step}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStep(@PathVariable String step) {
        List<Order> orders = getOrdersByStepUseCase.execute(step);
        return ResponseEntity.ok(orders.stream().map(OrderResponseDTO::fromDomain).collect(Collectors.toList()));
    }

    @Operation(description = "Cria um novo pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna novo pedido criado"),
            @ApiResponse(responseCode = "400", description = "Quando não é possível inserir o pedido")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        Order newOrder = OrderRequestDTO.toDomain(orderRequestDTO);

        try {
            // Aqui o Java aceita porque MercadoPagoPaymentResponseDTO é um Object
            MercadoPagoPaymentResponseDTO response = createOrderUseCase.execute(newOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            System.err.println("Falha na integração Mercado Pago: " + e.getMessage());

            // Agora o Java aceita retornar 'newOrder' porque Order também é um Object!
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(newOrder);
        }
    }

    @Operation(description = "Avança o status do pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna pedido no novo status"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @PutMapping("/next/{id}")
    public ResponseEntity<OrderResponseDTO> advanceStep(@PathVariable Long id) {
        try {
            Order updatedOrder = advanceOrderStatusUseCase.execute(id);
            return ResponseEntity.ok(OrderResponseDTO.fromDomain(updatedOrder));
        } catch (OrderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(description = "Efetua o cancelamento do pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna pedido cancelado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long id) {
        try {
            Order canceledOrder = cancelOrderUseCase.execute(id);
            return ResponseEntity.ok(OrderResponseDTO.fromDomain(canceledOrder));
        } catch (OrderNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @GetMapping("/pagamento/{orderId}")
    @Operation(summary = "Consulta o status de pagamento de um pedido por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status de pagamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<com.example.order.infrastructure.adapters.in.web.Dtos.PaymentStatusResponseDTO> getPaymentStatus(@PathVariable Long orderId) {
        Optional<Order> orderOptional = findOrderUseCase.execute(orderId);

        return orderOptional.map(order -> {
            com.example.order.infrastructure.adapters.in.web.Dtos.PaymentStatusResponseDTO response = new com.example.order.infrastructure.adapters.in.web.Dtos.PaymentStatusResponseDTO(order.getPaymentStatus());
            return ResponseEntity.ok(response);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado com o ID: " + orderId));
    }
}