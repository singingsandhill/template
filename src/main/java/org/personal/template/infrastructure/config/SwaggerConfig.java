package org.personal.template.infrastructure.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	private static final String SECURITY_SCHEME_NAME = "JWT";

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.openapi("3.0.3")
			.info(apiInfo())
			.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
			.components(securityComponents())
			.servers(Arrays.asList(
				new Server().url("/").description("기본 URL")));
	}

	private Info apiInfo() {
		return new Info()
			.title("API 문서")
			.version("1.0.0")
			.description("문서입니다.")
			.contact(new Contact()
					.name("Kim")
				//.email("")
				//.url("")
			);
	}

	private Components securityComponents() {
		return new Components()
			.addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT"));
	}
}
