package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI demoOpenAPI() {
        return new OpenAPI().info(new Info()
            .title("Spring Kafka + Redis 50 Examples")
            .version("1.0.0")
            .description("Learning API for Spring Boot, Kafka, Redis and Swagger/OpenAPI."));
    }
}
