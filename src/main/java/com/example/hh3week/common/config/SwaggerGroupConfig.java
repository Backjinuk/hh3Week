package com.example.hh3week.common.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerGroupConfig {

	@Bean
	public GroupedOpenApi publicApiV1() {
		return GroupedOpenApi.builder()
			.group("v1")
			.pathsToMatch("/api/v1/**")
			.build();
	}

	@Bean
	public GroupedOpenApi publicApiV2() {
		return GroupedOpenApi.builder()
			.group("v2")
			.pathsToMatch("/api/v2/**")
			.build();
	}
}
