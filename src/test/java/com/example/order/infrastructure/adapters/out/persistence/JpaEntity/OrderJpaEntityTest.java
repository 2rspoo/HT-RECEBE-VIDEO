package com.example.order.infrastructure.adapters.out.persistence.JpaEntity;

import com.example.order.domain.entities.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderJpaEntityTest {

    @Test
    @DisplayName("Deve converter corretamente de Entity para Domain (toDomain)")
    void deveConverterParaDominio() {
        // Arrange
        LocalDate data = LocalDate.of(2023, 10, 5);
        LocalTime hora = LocalTime.of(15, 30);

        OrderJpaEntity entity = new OrderJpaEntity(
                123L,
                456L,
                "PRONTO",
                data,
                hora,
                100,
                "Sem picles",
                "PAGO"
        );

        // Act
        Order domain = entity.toDomain();

        // Assert
        assertThat(domain.getId()).isEqualTo(123L);
        assertThat(domain.getIdcustomer()).isEqualTo(456L);
        assertThat(domain.getStep()).isEqualTo("PRONTO");
        assertThat(domain.getDate()).isEqualTo(data);
        assertThat(domain.getTime()).isEqualTo(hora);
        assertThat(domain.getPrice()).isEqualTo(100);
        assertThat(domain.getDetails()).isEqualTo("Sem picles");
        assertThat(domain.getPaymentStatus()).isEqualTo("PAGO");
    }

    @Test
    @DisplayName("Deve converter corretamente de Domain para Entity (fromDomain)")
    void deveConverterDeDominio() {
        // Arrange
        Order domain = new Order(
                777L,
                888L,
                "RECEBIDO",
                LocalDate.now(),
                LocalTime.now(),
                500,
                "Completo",
                "PENDENTE"
        );

        // Act
        OrderJpaEntity entity = OrderJpaEntity.fromDomain(domain);

        // Assert
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getIdcustomer()).isEqualTo(domain.getIdcustomer());
        assertThat(entity.getStep()).isEqualTo(domain.getStep());
        assertThat(entity.getDate()).isEqualTo(domain.getDate());
        assertThat(entity.getTime()).isEqualTo(domain.getTime());
        assertThat(entity.getPrice()).isEqualTo(domain.getPrice());
        assertThat(entity.getDetails()).isEqualTo(domain.getDetails());
        assertThat(entity.getPaymentStatus()).isEqualTo(domain.getPaymentStatus());
    }

    // --- TESTES DOS CONVERSORES (IMPORTANTE PARA DYNAMODB) ---

    @Test
    @DisplayName("LocalDateConverter - Deve converter LocalDate para String e vice-versa")
    void deveTestarConversorDeData() {
        // Arrange
        OrderJpaEntity.LocalDateConverter converter = new OrderJpaEntity.LocalDateConverter();
        LocalDate dataOriginal = LocalDate.of(2024, 12, 25);
        String dataStringEsperada = "2024-12-25";

        // Act & Assert (Ida)
        String stringConvertida = converter.convert(dataOriginal);
        assertThat(stringConvertida).isEqualTo(dataStringEsperada);

        // Act & Assert (Volta)
        LocalDate dataReconvertida = converter.unconvert(stringConvertida);
        assertThat(dataReconvertida).isEqualTo(dataOriginal);
    }

    @Test
    @DisplayName("LocalDateConverter - Deve lidar com nulos corretamente")
    void deveTestarConversorDeDataNulo() {
        OrderJpaEntity.LocalDateConverter converter = new OrderJpaEntity.LocalDateConverter();

        assertThat(converter.convert(null)).isNull();
        assertThat(converter.unconvert(null)).isNull();
    }

    @Test
    @DisplayName("LocalTimeConverter - Deve converter LocalTime para String e vice-versa")
    void deveTestarConversorDeHora() {
        // Arrange
        OrderJpaEntity.LocalTimeConverter converter = new OrderJpaEntity.LocalTimeConverter();
        LocalTime horaOriginal = LocalTime.of(14, 30, 0); // 14:30:00
        String horaStringEsperada = "14:30"; // O toString do LocalTime pode variar dependendo dos segundos, vamos verificar o parse

        // Act (Ida)
        String stringConvertida = converter.convert(horaOriginal);
        // O LocalTime.toString() padrão segue ISO-8601 (HH:mm ou HH:mm:ss)
        assertThat(stringConvertida).startsWith("14:30");

        // Act (Volta)
        LocalTime horaReconvertida = converter.unconvert(stringConvertida);
        assertThat(horaReconvertida).isEqualTo(horaOriginal);
    }

    @Test
    @DisplayName("LocalTimeConverter - Deve lidar com nulos corretamente")
    void deveTestarConversorDeHoraNulo() {
        OrderJpaEntity.LocalTimeConverter converter = new OrderJpaEntity.LocalTimeConverter();

        assertThat(converter.convert(null)).isNull();
        assertThat(converter.unconvert(null)).isNull();
    }
}