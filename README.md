Grupo 82

Contribuições: Camila Rabello Spoo Goshima - Discord: camilaspoo - 11 973091025 | 
Rodrigo Rabello Spoo - Discord: srsinistro9459 - 11 981046096


🎬 Recebe Vídeo (Order API)

Este projeto é um microsserviço responsável por ser a porta de entrada dos vídeos enviados pelos usuários. Ele recebe o arquivo, armazena no AWS S3, registra o estado inicial no DynamoDB e publica um evento na fila SQS para processamento assíncrono. Além disso, ele escuta a fila de resultados para atualizar o banco de dados, gerar links de download seguros (Pre-signed URLs) e notificar o usuário via AWS SES em caso de erro.

O projeto segue estritamente os princípios da Arquitetura Hexagonal (Ports and Adapters) para garantir manutenibilidade e total desacoplamento da infraestrutura em nuvem.

🏛️ Arquitetura

A aplicação foi desenhada para isolar o domínio das implementações externas:

Domain: Entidades centrais (VideoMetadata, Customer) e imutabilidade (uso de records).

Application (Use Cases): Casos de uso como UploadVideoUseCase, ListarVideosUseCase, AtualizarStatusUseCase.

Ports (In/Out): Contratos que definem a comunicação com S3 (VideoStoragePort), SQS (VideoQueuePort), DynamoDB (VideoRepositoryPort) e SES (EmailPort).

Infrastructure (Adapters): Implementações reais usando AWS SDK v2, adaptadores HTTP (RestTemplate) e Controladores REST.

🛠️ Tecnologias Utilizadas

Linguagem: Java 21

Framework: Spring Boot 3.4.1

Cloud/AWS: S3 (Armazenamento), SQS (Mensageria), DynamoDB (NoSQL), SES (E-mail)

Documentação: SpringDoc OpenAPI (Swagger)

Qualidade: JaCoCo (Cobertura de testes), SonarQube

Containerização: Docker & Kubernetes

🚀 Como Rodar o Projeto

Pré-requisitos

Java 21 SDK

Maven

Docker (opcional)

Credenciais da AWS configuradas (~/.aws/credentials)

Configuração de Ambiente
Configure as variáveis necessárias no arquivo application.properties ou via variáveis de ambiente do Kubernetes:

aws.region=us-east-1
aws.s3.bucket=seu-bucket-aqui
aws.sqs.url=url-da-fila-de-envio
aws.sqs.results-queue-url=url-da-fila-de-respostas
aws.ses.sender=seu-email-verificado@dominio.com
api.clientes.url=http://url-do-loadbalancer-de-clientes


Executando a Aplicação

mvn spring-boot:run


🧪 Testes e Qualidade =)

O projeto conta com testes unitários para validar regras de domínio, use cases e adaptadores.

Rodar Testes

mvn clean test


Relatório de Cobertura (JaCoCo): Após a execução dos testes, o relatório de cobertura pode ser visualizado em: target/site/jacoco/index.html

🥒 BDD (Behavior Driven Development)

Além dos testes unitários, a aplicação utiliza Cucumber para testes de comportamento, garantindo que as funcionalidades atendam aos requisitos de negócio descritos em linguagem natural (Gherkin).

Estrutura dos Testes:

Features (.feature): Localizados em src/test/resources/features. Descrevem os cenários (ex: Receber Vídeo, Atualizar Status).

Step Definitions: Localizados em src/test/java/.../bdd.

Como Rodar os Testes BDD:

mvn clean test


Relatório do Cucumber gerado em: target/cucumber-reports/cucumber.html

Análise de Código (SonarQube):

mvn clean verify sonar:sonar -Dsonar.token=SEU_TOKEN 


🔌 API Endpoints (Resumo)

Método

Endpoint

Descrição

POST

/v1/videos/upload

Recebe o vídeo (Multipart) e envia para a fila. (Requer Header X-User-Id)

GET

/v1/videos/usuario/{userId}

Lista todos os vídeos e status de um cliente específico.

GET

/api/videos/{id}/status

Retorna o status atual do vídeo e o link de download (.zip) temporário se concluído.

Acesso ao Frontend da Aplicação:
Abra o arquivo index.html diretamente no seu navegador. A interface unificada gerenciará o login, uploads e listagem conectando-se aos LoadBalancers automaticamente.

Acesso a Documentação da API (Swagger UI):
A documentação completa (Swagger) pode ser acessada em: http://localhost:8080/swagger-ui.html
