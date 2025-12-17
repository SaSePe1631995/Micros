package com.itm.space.backend.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebTestClient
class BackendGatewayClientApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void unauthenticatedRequestIsRedirectedToOauthLogin() {
		webTestClient.get()
				.uri("/api/users/hello")
				.exchange()
				.expectStatus().is3xxRedirection()
				.expectHeader()
				.valueEquals(
						"Location",
						"/oauth2/authorization/backend-gateway-client"
				);
	}

	@Test
	void actuatorIsAlsoProtectedByOauth2Client() {
		webTestClient.get()
				.uri("/actuator/health")
				.exchange()
				.expectStatus().is3xxRedirection();
	}
}

