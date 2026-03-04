Grupo 96

Contribuições:
Camila Rabello Spoo Goshima - Discord: camilaspoo - 11 973091025

Rodrigo Rabello Spoo - Discord: srsinistro9459 - 11 981046096

Vídeo:
https://www.youtube.com/watch?v=YQabQ-ai_8I

Repositório:
https://github.com/2rspoo/gestao-pedidos

## 🍔 API de Gestão de Pedidos 
Este projeto é um microsserviço para gestão de pedidos de uma lanchonete, desenvolvido seguindo os princípios da **Arquitetura Hexagonal (Ports and Adapters)**. O sistema gerencia o ciclo de vida do pedido, desde a recepção, pagamento (integração com Mercado Pago), preparação até a finalização.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-green)
![Coverage](https://img.shields.io/badge/Coverage-Jacoco-success)
![Build](https://img.shields.io/badge/Build-Maven-blue)

## 🏛️ Arquitetura

O projeto foi estruturado para garantir o desacoplamento entre a regra de negócio e as tecnologias externas:

* **Domain:** Entidades e regras de negócio centrais (`Order`, etc).
* **Application (Use Cases):** Implementação dos casos de uso (`CreateOrder`, `AdvanceStatus`, `GetAllOrders`, etc).
* **Ports (In/Out):** Interfaces que definem a entrada e saída do core da aplicação.
* **Infrastructure (Adapters):** Implementações reais das portas (DynamoDB Repository, Mercado Pago Service, Rest Controllers).

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3.4.1
* **Banco de Dados:** Amazon DynamoDB
* **Pagamentos:** Integração via QR Code com API do Mercado Pago
* **Testes:** JUnit 5, Mockito
* **Qualidade de Código:** JaCoCo (Cobertura), SonarQube
* **Containerização:** Docker (Opcional para ambiente local)

## 🚀 Como Rodar o Projeto

### Pré-requisitos
* Java 21 SDK instalado
* Maven instalado
* Docker (para rodar o SonarQube localmente, se desejar)

### Configuração de Ambiente
Crie um arquivo `application-prod.properties` ou configure as variáveis de ambiente necessárias para a integração com o Mercado Pago e DynamoDB:

## DynamoDB (Local ou AWS)
aws.access.key=SEU_ACCESS_KEY
aws.secret.key=SEU_SECRET_KEY
aws.region=us-east-1

## Mercado Pago
mercadopago.api.url=[https://api.mercadopago.com](https://api.mercadopago.com)
mercadopago.access.token=SEU_ACCESS_TOKEN
mercadopago.user.id=SEU_USER_ID
mercadopago.pos.id=SEU_POS_ID
mercadopago.webhook.url=SEU_WEBHOOK_URL
Executando a Aplicação
Bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod

## 🧪 Testes e Qualidade
O projeto possui uma suíte de testes unitários robusta cobrindo Use Cases, Domain, Adapters e Controllers.
Rodar Testes
Bash
mvn clean test
Relatório de Cobertura (JaCoCo)
Após rodar os testes, o relatório HTML estará disponível em:
target/site/jacoco/index.html
http://localhost:63342/gestao-pedidos/cardapio/target/site/jacoco/index.html?_ijt=og6voocemr71mb4umrtner7065&_ij_reload=RELOAD_ON_SAVE
<img width="1315" height="320" alt="image" src="https://github.com/user-attachments/assets/75847dd7-53ac-45fb-b6ab-98a1abacd503" />


### Análise de Código (SonarQube)
Para enviar as métricas para o SonarQube (certifique-se de ter um servidor Sonar rodando):
Bash
mvn clean verify sonar:sonar -Dsonar.token=SEU_TOKEN
<img width="1125" height="193" alt="image" src="https://github.com/user-attachments/assets/95fe07d0-8185-4a0f-849f-03936c152654" />

### 📋 Fluxo do Pedido (Status)
O sistema segue uma máquina de estados estrita para garantir a consistência:
1.	RECEBIDO: Pedido criado, aguardando pagamento.
2.	PREPARO: Pagamento confirmado via Webhook, enviado para cozinha.
3.	PRONTO: Preparo finalizado, aguardando retirada.
4.	FINALIZADO: Pedido entregue ao cliente.
5.	CANCELADO: Caso o pagamento seja recusado ou cancelado manualmente.
   
Nota: A listagem de pedidos (GetAllOrders) prioriza pedidos PRONTOS > PREPARO > RECEBIDO e filtra os finalizados/cancelados.

##🔌 API Endpoints (Resumo)
Método	Endpoint	Descrição
POST	/orders	Cria um novo pedido e gera QR Code
GET	/orders	Lista fila de pedidos (Ordenada por prioridade)
GET	/orders/{id}	Busca detalhes de um pedido
PATCH	/orders/{id}/advance	Avança o status do pedido
POST	/webhook/payment	Recebe notificação de pagamento do Mercado Pago
______________________________________________________________________________

## Acesso ao Frontend da Aplicação:

Abra o arquivo index.html, webhook ou stress.html diretamente no seu navegador. As interfaces carregarão os dados da API.







