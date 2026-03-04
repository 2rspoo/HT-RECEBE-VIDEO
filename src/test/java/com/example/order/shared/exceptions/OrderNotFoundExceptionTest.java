package com.example.order.shared.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderNotFoundExceptionTest {

    @Test
    @DisplayName("Deve criar a exceção com a mensagem correta")
    void deveCriarExcecaoComMensagem() {
        // Arrange
        String mensagemEsperada = "Pedido 123 não encontrado no sistema";

        // Act
        OrderNotFoundException exception = new OrderNotFoundException(mensagemEsperada);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensagemEsperada);
        assertThat(exception).isInstanceOf(RuntimeException.class); // Garante que é uma Unchecked Exception
    }
}