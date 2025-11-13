package com.williammedina.content_analysis_service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Content Validation Service")
                        .version("1.0")
                        .description("Microservicio para la validación de contenido inapropiado en la plataforma ForoHub.")
                );
    }
}
