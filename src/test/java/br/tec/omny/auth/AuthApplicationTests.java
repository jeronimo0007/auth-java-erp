package br.tec.omny.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.profiles.active=test"
})
class AuthApplicationTests {

	@Test
	void contextLoads() {
		// Teste básico para verificar se o contexto Spring carrega corretamente
	}

}
