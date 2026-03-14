package com.example.order.infrastructure.adapters.out.persistence.JpaAdapter;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.example.order.domain.entities.Order;
import com.example.order.infrastructure.adapters.out.persistence.JpaEntity.OrderJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDynamoAdapterTest {

    @Mock
    private AmazonDynamoDB amazonDynamoDB;

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @Mock
    private PaginatedScanList<OrderJpaEntity> paginatedScanList;

    private OrderDynamoAdapter orderDynamoAdapter;

    @BeforeEach
    void setUp() {
        orderDynamoAdapter = new OrderDynamoAdapter(amazonDynamoDB);
        ReflectionTestUtils.setField(orderDynamoAdapter, "dynamoDBMapper", dynamoDBMapper);
    }

    // Cria um objeto de domínio para teste
    private Order criarOrderDominio() {
        return new Order(null, 1L, "RECEBIDO", 5000, "Detalhes");
    }

    // CORREÇÃO: Cria a entidade JPA usando setters para evitar erro de construtor
    private OrderJpaEntity criarEntity() {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(123456L);
        entity.setIdcustomer(1L); // Ajustado para idcustomer
        entity.setStep("RECEBIDO");
        entity.setPrice(5000);
        // Preencha outros campos se necessário (datas, paymentStatus), mas estes bastam para o teste
        return entity;
    }

    @Test
    @DisplayName("save - Deve gerar ID, converter e salvar no DynamoDB")
    void deveSalvarPedido() {
        // Arrange
        Order orderParaSalvar = criarOrderDominio();
        assertThat(orderParaSalvar.getId()).isNull();

        // Act
        Order resultado = orderDynamoAdapter.save(orderParaSalvar);

        // Assert
        ArgumentCaptor<OrderJpaEntity> captor = ArgumentCaptor.forClass(OrderJpaEntity.class);
        verify(dynamoDBMapper).save(captor.capture());

        OrderJpaEntity entitySalva = captor.getValue();

        assertThat(entitySalva.getId()).isNotNull();
        assertThat(resultado.getId()).isNotNull();

        // CORREÇÃO: Verificando IdCustomer em vez de Category
        assertThat(entitySalva.getIdcustomer()).isEqualTo(orderParaSalvar.getIdcustomer());
    }

    @Test
    @DisplayName("findById - Deve retornar Optional com Order quando encontrado")
    void deveEncontrarPorId() {
        // Arrange
        Long id = 123L;
        OrderJpaEntity entityEncontrada = criarEntity();

        when(dynamoDBMapper.load(OrderJpaEntity.class, id)).thenReturn(entityEncontrada);

        // Act
        Optional<Order> resultado = orderDynamoAdapter.findById(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(entityEncontrada.getId());
        verify(dynamoDBMapper).load(OrderJpaEntity.class, id);
    }

    @Test
    @DisplayName("findById - Deve retornar vazio quando não encontrado")
    void deveRetornarVazioSeNaoEncontrar() {
        // Arrange
        Long id = 999L;
        when(dynamoDBMapper.load(OrderJpaEntity.class, id)).thenReturn(null);

        // Act
        Optional<Order> resultado = orderDynamoAdapter.findById(id);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("findAll - Deve fazer scan e retornar lista")
    void deveListarTodos() {
        // Arrange
        OrderJpaEntity entity = criarEntity();

        when(paginatedScanList.stream()).thenReturn(Stream.of(entity));
        when(dynamoDBMapper.scan(eq(OrderJpaEntity.class), any(DynamoDBScanExpression.class)))
                .thenReturn(paginatedScanList);

        // Act
        List<Order> resultado = orderDynamoAdapter.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(entity.getId());

        verify(dynamoDBMapper).scan(eq(OrderJpaEntity.class), any(DynamoDBScanExpression.class));
    }

    @Test
    @DisplayName("findByStep - Deve fazer scan com filtro de expressão")
    void deveListarPorStep() {
        // Arrange
        String step = "PRONTO";
        OrderJpaEntity entity = criarEntity();

        when(paginatedScanList.stream()).thenReturn(Stream.of(entity));

        ArgumentCaptor<DynamoDBScanExpression> captor = ArgumentCaptor.forClass(DynamoDBScanExpression.class);

        when(dynamoDBMapper.scan(eq(OrderJpaEntity.class), captor.capture()))
                .thenReturn(paginatedScanList);

        // Act
        List<Order> resultado = orderDynamoAdapter.findByStep(step);

        // Assert
        assertThat(resultado).hasSize(1);

        DynamoDBScanExpression expressionUsada = captor.getValue();
        assertThat(expressionUsada.getFilterExpression()).contains("step = :v1");
        assertThat(expressionUsada.getExpressionAttributeValues().get(":v1").getS()).isEqualTo(step);
    }
}