package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI demoOpenAPI() {
        // springdoc-openapi가 이 메타데이터를 /v3/api-docs와 Swagger UI에 노출한다.
        return new OpenAPI().info(new Info()
            .title("Spring Kafka + Redis 50 Examples")
            .version("1.0.0")
            .description("Learning API for Spring Boot, Kafka, Redis and Swagger/OpenAPI."));
    }
}
