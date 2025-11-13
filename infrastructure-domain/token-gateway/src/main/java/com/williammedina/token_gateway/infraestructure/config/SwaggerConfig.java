package com.williammedina.token_gateway.infraestructure.config;

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
                        .title("Token Gateway")
                        .version("1.0")
                        .description("Microservicio que gestiona la comunicación y autorización entre el frontend y el servidor de autenticación en la plataforma ForoHub.")
                );
    }
}
