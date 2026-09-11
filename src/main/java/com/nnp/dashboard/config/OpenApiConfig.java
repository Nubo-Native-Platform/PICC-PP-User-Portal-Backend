package com.nnp.dashboard.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * OpenAPI 3.0 / Swagger UI Configuration for PICC-PP-User-Portal-Backend.
 */
@Configuration
public class OpenApiConfig {

	@Value("${server.port:8080}")
	private String serverPort;

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("PICC-PP-User-Portal-Backend REST API")
						.version("0.0.1-SNAPSHOT")
						.description("""
								Enterprise REST API for Nubo Native Platform (NNP) User Portal Backend.
								
								### Key Capabilities:
								* **Dynamic UI Navigation Hierarchy**: Hierarchical environment, feature, and element trees for tenant workspaces.
								* **User URL Accessibility & Redis Caching**: Sub-millisecond authorization check for screen permissions.
								* **Tenant Accounts & Billing**: Account profile tracking, subscriptions, payment logs, and proxy endpoint configs.
								* **Support Ticket Integration (Redmine)**: Direct issue ticketing lifecycle, categories, priorities, and historical trends.
								* **Observability & Resource Telemetry**: Real-time cluster usage metrics, pod telemetry, and Kafka metric streaming.
								""")
						.contact(new Contact()
								.name("Nubo Native Platform Team")
								.email("contribution@nubons.com")
								.url("https://github.com/Nubo-Native-Platform/PICC-PP-User-Portal-Backend"))
						.license(new License()
								.name("Apache License 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0")))
				.servers(List.of(
						new Server().url("/").description("Default Server / Current Host"),
						new Server().url("http://localhost:" + serverPort).description("Local Development Server")
				))
				.tags(List.of(
						new Tag().name("Accounts & Billing").description("Tenant account profiles, subscriptions, payment logs, and proxies"),
						new Tag().name("UI Rendering & Navigation").description("Multi-level feature element hierarchies and screen accessibility"),
						new Tag().name("Support & Ticketing").description("Redmine issue tracking, status updates, and monthly trends"),
						new Tag().name("Observability & Metrics").description("Pod usage metrics, telemetry aggregations, and log streaming")
				));
	}
}
