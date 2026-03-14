package com.example.order.infrastructure.adapters.in.web.Dtos;

import com.example.order.domain.entities.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class OrderRequestDTOTest {

    @Autowired
    private JacksonTester<OrderRequestDTO> json;

    @Test
    @DisplayName("Deve converter DTO para Domínio aplicando as regras de inicialização")
    void deveConverterParaDominioCorretamente() {
        // Arrange
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setIdcustomer(55L);
        dto.setPrice(100);
        dto.setDetails("Sem molho");
        // O campo 'step' no DTO é ignorado na conversão segundo sua lógica atual,
        // mas vamos setar para garantir que ele é sobrescrito por "RECEBIDO"
        dto.setStep("QUALQUER_COISA");

        // Act
        Order domain = OrderRequestDTO.toDomain(dto);

        // Assert - Campos copiados
        assertThat(domain.getIdcustomer()).isEqualTo(55L);
        assertThat(domain.getPrice()).isEqualTo(100);
        assertThat(domain.getDetails()).isEqualTo("Sem molho");

        // Assert - Regras de Negócio (Hardcoded no método toDomain)
        assertThat(domain.getStep()).isEqualTo("RECEBIDO"); // Deve forçar RECEBIDO
        assertThat(domain.getPaymentStatus()).isEqualTo("PENDENTE"); // Deve forçar PENDENTE

        // Assert - Data e Hora (Gerados no momento)
        assertThat(domain.getDate()).isNotNull().isEqualTo(LocalDate.now());
        assertThat(domain.getTime()).isNotNull();
    }

    @Test
    @DisplayName("Deve deserializar JSON para DTO corretamente")
    void deveDeserializarJson() throws Exception {
        // Arrange
        String jsonContent = """
                {
                    "idcustomer": 10,
                    "step": "IGNORE_THIS",
                    "price": 5000,
                    "details": "Batata extra"
                }
                """;

        // Act
        OrderRequestDTO dto = json.parseObject(jsonContent);

        // Assert
        assertThat(dto.getIdcustomer()).isEqualTo(10L);
        assertThat(dto.getPrice()).isEqualTo(5000);
        assertThat(dto.getDetails()).isEqualTo("Batata extra");
        assertThat(dto.getStep()).isEqualTo("IGNORE_THIS");
    }

    @Test
    @DisplayName("Deve serializar DTO para JSON corretamente")
    void deveSerializarJson() throws Exception {
        // Arrange
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setIdcustomer(99L);
        dto.setPrice(200);
        dto.setDetails("Teste");
        dto.setStep("TESTE");

        // Act
        JsonContent<OrderRequestDTO> result = json.write(dto);

        // Assert
        assertThat(result).extractingJsonPathNumberValue("$.idcustomer").isEqualTo(99);
        assertThat(result).extractingJsonPathNumberValue("$.price").isEqualTo(200);
        assertThat(result).extractingJsonPathStringValue("$.details").isEqualTo("Teste");
        assertThat(result).extractingJsonPathStringValue("$.step").isEqualTo("TESTE");
    }
}