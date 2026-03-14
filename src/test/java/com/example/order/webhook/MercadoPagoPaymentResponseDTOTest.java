package com.example.order.webhook;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class MercadoPagoPaymentResponseDTOTest {

    @Autowired
    private JacksonTester<MercadoPagoPaymentResponseDTO> json;

    @Test
    @DisplayName("Deve serializar para JSON com a chave 'qr_data' (snake_case)")
    void deveSerializarJsonCorretamente() throws Exception {
        // Arrange
        MercadoPagoPaymentResponseDTO dto = new MercadoPagoPaymentResponseDTO();
        dto.setStatus("CRIADO");
        dto.setMessage("Aguardando pagamento");
        dto.setQrData("00020101021243650016COM.MERCADOLIBRE...");

        // Act
        JsonContent<MercadoPagoPaymentResponseDTO> result = json.write(dto);

        // Assert
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("CRIADO");
        assertThat(result).extractingJsonPathStringValue("$.message").isEqualTo("Aguardando pagamento");

        // VERIFICAÇÃO PRINCIPAL: O Java 'qrData' virou JSON 'qr_data'
        assertThat(result).extractingJsonPathStringValue("$.qr_data").isEqualTo("00020101021243650016COM.MERCADOLIBRE...");

        // Garante que o nome camelCase não vazou
        assertThat(result).doesNotHaveJsonPath("$.qrData");
    }

    @Test
    @DisplayName("Deve deserializar JSON lendo da chave 'qr_data'")
    void deveDeserializarJsonCorretamente() throws Exception {
        // Arrange
        String jsonContent = """
                {
                    "status": "APROVADO",
                    "message": "Sucesso",
                    "qr_data": "DADOS_DO_QR_CODE_AQUI"
                }
                """;

        // Act
        MercadoPagoPaymentResponseDTO dto = json.parseObject(jsonContent);

        // Assert
        assertThat(dto.getStatus()).isEqualTo("APROVADO");
        assertThat(dto.getMessage()).isEqualTo("Sucesso");

        // Verifica se leu o campo com underscore e jogou no atributo Java correto
        assertThat(dto.getQrData()).isEqualTo("DADOS_DO_QR_CODE_AQUI");
    }
}