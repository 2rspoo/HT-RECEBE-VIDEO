package com.example.order.infrastructure.adapters.in.web.Dtos;

import com.example.order.domain.entities.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class OrderResponseDTOTest {

    @Autowired
    private JacksonTester<OrderResponseDTO> json;

    @Test
    @DisplayName("Deve mapear corretamente de Entidade para DTO (fromDomain)")
    void deveMapearDeDominio() {
        // Arrange
        LocalDate data = LocalDate.of(2023, 12, 25);
        LocalTime hora = LocalTime.of(20, 30);

        Order entity = new Order(
                1L,
                10L,
                "PRONTO",
                data,
                hora,
                5000,
                "Sem cebola",
                "APROVADO" // Testando o novo campo
        );

        // Act
        OrderResponseDTO dto = OrderResponseDTO.fromDomain(entity);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getIdcustomer()).isEqualTo(10L);
        assertThat(dto.getStep()).isEqualTo("PRONTO");
        assertThat(dto.getDate()).isEqualTo(data);
        assertThat(dto.getTime()).isEqualTo(hora);
        assertThat(dto.getPrice()).isEqualTo(5000);
        assertThat(dto.getDetails()).isEqualTo("Sem cebola");
        assertThat(dto.getPaymentStatus()).isEqualTo("APROVADO"); // Validação crucial
    }

    @Test
    @DisplayName("Deve serializar DTO para JSON corretamente")
    void deveSerializarParaJson() throws Exception {
        // Arrange
        OrderResponseDTO dto = new OrderResponseDTO(
                5L,
                99L,
                "RECEBIDO",
                LocalDate.of(2024, 1, 1),
                LocalTime.of(12, 0),
                2500,
                "Caprichado",
                "PENDENTE"
        );

        // Act
        JsonContent<OrderResponseDTO> result = json.write(dto);

        // Assert
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.idcustomer").isEqualTo(99);
        assertThat(result).extractingJsonPathStringValue("$.step").isEqualTo("RECEBIDO");
        assertThat(result).extractingJsonPathStringValue("$.date").isEqualTo("2024-01-01");
        assertThat(result).extractingJsonPathStringValue("$.time").isEqualTo("12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.price").isEqualTo(2500);
        assertThat(result).extractingJsonPathStringValue("$.details").isEqualTo("Caprichado");
        assertThat(result).extractingJsonPathStringValue("$.paymentStatus").isEqualTo("PENDENTE");
    }
}