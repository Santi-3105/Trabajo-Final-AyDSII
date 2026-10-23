package com.aydsii.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Título y descripción que se muestran en Swagger UI
 * (http://localhost:8080/swagger-ui/index.html).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Trabajo Final AyDS II - API")
                .version("1.0")
                .description("API del caso de uso Comprar"));
    }
}
