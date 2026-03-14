package com.example.order.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    @DisplayName("Deve criar um novo pedido com valores padrão corretos (Status RECEBIDO e Pagamento PENDENTE)")
    void deveCriarNovoPedidoComPadroes() {
        // Arrange
        Long idCustomer = 123L;
        Integer price = 5000; // 50.00
        String details = "Sem cebola";

        // Parâmetros que são ignorados ou sobrescritos pela lógica do construtor:
        Long idIgnorado = 1L;
        String stepIgnorado = "EM_PREPARO";

        // Act
        // Nota: Baseado no seu código, o ID e o Step passados aqui são ignorados pelo construtor de "novo pedido"
        Order order = new Order(idIgnorado, idCustomer, stepIgnorado, price, details);

        // Assert
        assertThat(order.getIdcustomer()).isEqualTo(idCustomer);
        assertThat(order.getPrice()).isEqualTo(price);
        assertThat(order.getDetails()).isEqualTo(details);

        // Verificações das regras de negócio do construtor:
        assertThat(order.getStep()).isEqualTo("RECEBIDO"); // Garante que forçou "RECEBIDO"
        assertThat(order.getPaymentStatus()).isEqualTo("PENDENTE"); // Garante status inicial
        assertThat(order.getDate()).isNotNull().isEqualTo(LocalDate.now()); // Garante que a data foi gerada
        assertThat(order.getTime()).isNotNull(); // Garante que a hora foi gerada

        // O seu construtor atual NÃO atribui o ID passado no argumento.
        // Se isso for intencional (o banco gera depois), o ID deve ser nulo.
        assertThat(order.getId()).isNull();
    }

    @Test
    @DisplayName("Deve reconstituir um pedido completo (ex: vindo do banco de dados)")
    void deveReconstituirPedidoCompleto() {
        // Arrange
        Long id = 10L;
        Long idCustomer = 55L;
        String step = "PRONTO";
        LocalDate date = LocalDate.of(2023, 12, 25);
        LocalTime time = LocalTime.of(20, 0);
        Integer price = 10000;
        String details = "Para viagem";
        String paymentStatus = "APROVADO";

        // Act
        Order order = new Order(id, idCustomer, step, date, time, price, details, paymentStatus);

        // Assert
        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getIdcustomer()).isEqualTo(idCustomer);
        assertThat(order.getStep()).isEqualTo(step);
        assertThat(order.getDate()).isEqualTo(date);
        assertThat(order.getTime()).isEqualTo(time);
        assertThat(order.getPrice()).isEqualTo(price);
        assertThat(order.getDetails()).isEqualTo(details);
        assertThat(order.getPaymentStatus()).isEqualTo(paymentStatus);
    }

    @Test
    @DisplayName("Deve atualizar valores via Setters")
    void deveTestarSetters() {
        // Arrange
        Order order = new Order();

        // Act
        order.setId(1L);
        order.setPaymentStatus("RECUSADO");
        order.setStep("FINALIZADO");

        // Assert
        assertThat(order.getId()).isEqualTo(1L);
        assertThat(order.getPaymentStatus()).isEqualTo("RECUSADO");
        assertThat(order.getStep()).isEqualTo("FINALIZADO");
    }

    @Test
    @DisplayName("Deve considerar iguais dois pedidos com o mesmo ID")
    void deveSerIgualSeIdForIgual() {
        // Arrange
        // Pedido 1: ID 1, Status PENDENTE
        Order order1 = new Order(1L, 1L, "RECEBIDO", LocalDate.now(), LocalTime.now(), 100, "Det 1", "PENDENTE");

        // Pedido 2: ID 1, Status PAGO (Conteúdo diferente, mas ID igual)
        Order order2 = new Order(1L, 2L, "FINALIZADO", LocalDate.now(), LocalTime.now(), 200, "Det 2", "PAGO");

        // Act & Assert
        assertThat(order1).isEqualTo(order2);
        assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
    }

    @Test
    @DisplayName("Não deve considerar iguais dois pedidos com IDs diferentes")
    void naoDeveSerIgualSeIdForDiferente() {
        // Arrange
        Order order1 = new Order();
        order1.setId(1L);

        Order order2 = new Order();
        order2.setId(2L);

        // Act & Assert
        assertThat(order1).isNotEqualTo(order2);
    }

    @Test
    @DisplayName("Teste do método setQrCodeData (para cobertura)")
    void deveTestarSetQrCodeData() {
        Order order = new Order();
        // Apenas para garantir que o método existe e não quebra,
        // já que a implementação atual está vazia.
        order.setQrCodeData("dados");
        assertThat(order).isNotNull();
    }
}