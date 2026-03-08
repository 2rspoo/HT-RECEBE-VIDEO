package com.example.order.webhook;

import com.example.order.infrastructure.adapters.in.web.Dtos.MercadoPagoItemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class MercadoPagoPaymentRequestDTOTest {

    @Autowired
    private JacksonTester<MercadoPagoPaymentRequestDTO> json;

    @Test
    @DisplayName("Deve serializar para JSON respeitando as anotações @JsonProperty")
    void deveSerializarCorretamente() throws Exception {
        // Arrange
        MercadoPagoPaymentRequestDTO dto = new MercadoPagoPaymentRequestDTO();
        dto.setExternalReference("pedido_123");
        dto.setTotalAmount(new BigDecimal("150.50"));
        dto.setDescription("Pedido Lanchonete");

        // Criando um item dummy para a lista
        MercadoPagoItemDTO item = new MercadoPagoItemDTO();
        item.setTitle("X-Burger");
        dto.setItems(Collections.singletonList(item));

        // Act
        JsonContent<MercadoPagoPaymentRequestDTO> result = json.write(dto);

        // Assert
        // Valida se as chaves JSON estão em snake_case (padrão Mercado Pago)
        assertThat(result).extractingJsonPathStringValue("$.external_reference").isEqualTo("pedido_123");
        assertThat(result).extractingJsonPathNumberValue("$.total_amount").isEqualTo(150.50);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Pedido Lanchonete");

        // Valida se a lista de itens foi incluída
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].title").isEqualTo("X-Burger");

        // Garante que NÃO vazou o nome do atributo Java (camelCase)
        assertThat(result).doesNotHaveJsonPath("$.externalReference");
        assertThat(result).doesNotHaveJsonPath("$.totalAmount");
    }

    @Test
    @DisplayName("Deve deserializar JSON para Objeto Java corretamente")
    void deveDeserializarCorretamente() throws Exception {
        // Arrange
        String jsonContent = """
                {
                    "external_reference": "REF_999",
                    "total_amount": 99.90,
                    "description": "Combo Familia",
                    "items": []
                }
                """;

        // Act
        MercadoPagoPaymentRequestDTO dto = json.parseObject(jsonContent);

        // Assert
        assertThat(dto.getExternalReference()).isEqualTo("REF_999");
        assertThat(dto.getTotalAmount()).isEqualTo(new BigDecimal("99.90"));
        assertThat(dto.getDescription()).isEqualTo("Combo Familia");
        assertThat(dto.getItems()).isEmpty();
    }
}