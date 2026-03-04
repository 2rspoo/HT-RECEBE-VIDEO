package com.example.order.infrastructure.adapters.in.web.Dtos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest // Carrega o Jackson para testar a conversão JSON <-> Objeto
class MercadoPagoItemDTOTest {

    @Autowired
    private JacksonTester<MercadoPagoItemDTO> json;

    @Test
    @DisplayName("Deve serializar para JSON com o campo 'unit_price' (snake_case)")
    void deveSerializarCorretamente() throws Exception {
        // Arrange
        MercadoPagoItemDTO dto = new MercadoPagoItemDTO(
                "X-Burger",
                "Com queijo",
                new BigDecimal("25.90"),
                2
        );

        // Act
        JsonContent<MercadoPagoItemDTO> result = json.write(dto);

        // Assert
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo("X-Burger");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Com queijo");
        assertThat(result).extractingJsonPathNumberValue("$.quantity").isEqualTo(2);

        // O TESTE MAIS IMPORTANTE:
        // Verifica se o Java 'unitPrice' virou 'unit_price' no JSON
        assertThat(result).extractingJsonPathNumberValue("$.unit_price").isEqualTo(25.90);

        // Garante que NÃO existe o campo com nome antigo (camelCase)
        assertThat(result).doesNotHaveJsonPath("$.unitPrice");
    }

    @Test
    @DisplayName("Deve deserializar JSON recebido convertendo 'unit_price' para o objeto Java")
    void deveDeserializarCorretamente() throws Exception {
        // Arrange
        String jsonContent = """
                {
                    "title": "Coca-Cola",
                    "description": "Lata",
                    "unit_price": 5.50,
                    "quantity": 1
                }
                """;

        // Act
        MercadoPagoItemDTO dto = json.parseObject(jsonContent);

        // Assert
        assertThat(dto.getTitle()).isEqualTo("Coca-Cola");
        assertThat(dto.getDescription()).isEqualTo("Lata");
        assertThat(dto.getQuantity()).isEqualTo(1);

        // Verifica se leu o campo com underscore e jogou no atributo correto
        assertThat(dto.getUnitPrice()).isEqualTo(new BigDecimal("5.50"));
    }

    @Test
    @DisplayName("Deve testar Getters, Setters e Construtor Vazio")
    void deveTestarEncapsulamento() {
        // Arrange
        MercadoPagoItemDTO dto = new MercadoPagoItemDTO();

        // Act
        dto.setTitle("Teste");
        dto.setDescription("Desc");
        dto.setUnitPrice(BigDecimal.TEN);
        dto.setQuantity(10);

        // Assert
        assertThat(dto.getTitle()).isEqualTo("Teste");
        assertThat(dto.getDescription()).isEqualTo("Desc");
        assertThat(dto.getUnitPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(dto.getQuantity()).isEqualTo(10);
    }
}