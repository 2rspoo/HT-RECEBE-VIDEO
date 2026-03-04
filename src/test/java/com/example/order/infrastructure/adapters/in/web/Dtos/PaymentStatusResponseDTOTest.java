package com.example.order.infrastructure.adapters.in.web.Dtos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class PaymentStatusResponseDTOTest {

    @Autowired
    private JacksonTester<PaymentStatusResponseDTO> json;

    @Test
    @DisplayName("Deve serializar para JSON corretamente")
    void deveSerializarJson() throws Exception {
        // Arrange
        String status = "APROVADO";
        PaymentStatusResponseDTO dto = new PaymentStatusResponseDTO(status);

        // Act
        JsonContent<PaymentStatusResponseDTO> result = json.write(dto);

        // Assert
        // Garante que o JSON gerado é: { "paymentStatus": "APROVADO" }
        assertThat(result).extractingJsonPathStringValue("$.paymentStatus").isEqualTo(status);
    }

    @Test
    @DisplayName("Deve testar getters e setters (Encapsulamento)")
    void deveTestarEncapsulamento() {
        // Arrange
        PaymentStatusResponseDTO dto = new PaymentStatusResponseDTO("PENDENTE");

        // Act
        dto.setPaymentStatus("RECUSADO");

        // Assert
        assertThat(dto.getPaymentStatus()).isEqualTo("RECUSADO");
    }
}