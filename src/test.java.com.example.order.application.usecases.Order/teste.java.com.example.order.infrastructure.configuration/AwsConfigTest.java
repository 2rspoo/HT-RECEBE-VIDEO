package com.example.order.infrastructure.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.ses.SesClient;

import static org.assertj.core.api.Assertions.assertThat;

class AwsConfigTest {

    // Cria um simulador do contexto do Spring
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(AwsConfig.class)
            .withPropertyValues(
                    // Passamos valores falsos para as variáveis exigidas pelo @Value
                    "AWS_REGION=sa-east-1",
                    "AWS_ACCESS_KEY_ID=minha-chave-falsa",
                    "AWS_SECRET_ACCESS_KEY=meu-segredo-falso"
            );

    @Test
    @DisplayName("Deve inicializar o contexto e registrar todos os Beans de infraestrutura com sucesso")
    void shouldLoadAllBeansSuccessfully() {
        // Executa o mini-Spring e verifica o que tem dentro dele
        contextRunner.run(context -> {
            // Verifica se a própria classe de configuração subiu sem erros
            assertThat(context).hasSingleBean(AwsConfig.class);

            // Verifica se todos os Beans da AWS foram criados e registrados
            assertThat(context).hasSingleBean(S3Client.class);
            assertThat(context).hasSingleBean(DynamoDbClient.class);
            assertThat(context).hasSingleBean(S3Presigner.class);
            assertThat(context).hasSingleBean(SesClient.class);

            // Verifica se o RestTemplate foi criado
            assertThat(context).hasSingleBean(RestTemplate.class);

            // Pega uma instância para ter 100% de certeza que não é nula
            assertThat(context.getBean(S3Client.class)).isNotNull();
            assertThat(context.getBean(DynamoDbClient.class)).isNotNull();
        });
    }
}