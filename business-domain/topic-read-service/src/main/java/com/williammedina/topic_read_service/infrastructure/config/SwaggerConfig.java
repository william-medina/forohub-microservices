package com.williammedina.topic_read_service.infrastructure.config;

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
                        .title("Topic Read Service")
                        .version("1.0")
                        .description("Microservicio que proporciona acceso de solo lectura a los tópicos y sus detalles en la plataforma ForoHub.")
                );
    }
}
