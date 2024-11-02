package com.example.hh3week.common.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
// import io.swagger.v3.oas.models.security.SecurityScheme; // 제거

@Configuration
@SecurityScheme(
	name = "bearerAuth",
	type = SecuritySchemeType.HTTP,
	scheme = "bearer",
	bearerFormat = "JWT"
)
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info().title("Concert Reservation API")
				.version("1.0")
				.description("콘서트 예약 시스템을 위한 API 문서"))
			.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
			.components(new io.swagger.v3.oas.models.Components()
				.addSecuritySchemes("bearerAuth",
					new io.swagger.v3.oas.models.security.SecurityScheme() // 완전한 클래스 이름 사용
						.type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")));
	}
}
