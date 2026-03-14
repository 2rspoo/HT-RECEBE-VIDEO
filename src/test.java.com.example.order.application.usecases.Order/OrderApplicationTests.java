package com.example.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Passamos propriedades falsas para o Spring Boot não quebrar no GitHub Actions
@SpringBootTest(properties = {
		"AWS_REGION=sa-east-1",
		"AWS_ACCESS_KEY_ID=fake-key",
		"AWS_SECRET_ACCESS_KEY=fake-secret",
		"aws.s3.bucket=meu-bucket-fake",
		"aws.sqs.url=http://localhost/fila-fake",
		"AWS_SQS_RESULT_URL=http://localhost/fila-resultado-fake",
		"CUSTOMER_API_URL=http://localhost:8080"
})
class OrderApplicationTests {

	@Test
	void contextLoads() {
		// Se o Spring conseguir ler as propriedades falsas acima e subir sem quebrar, o teste passa!
	}

}