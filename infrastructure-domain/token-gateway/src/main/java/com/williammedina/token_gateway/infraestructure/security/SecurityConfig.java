package com.williammedina.token_gateway.infraestructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${app.frontend.url}")
    private String allowedOrigin;

    @Bean
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new CorsConfiguration();
                corsConfig.setAllowedOrigins(List.of(allowedOrigin));
                corsConfig.setAllowedMethods(List.of("POST", "OPTIONS"));
                corsConfig.setAllowedHeaders(List.of("Content-Type", "Authorization"));
                corsConfig.setAllowCredentials(true);
                return corsConfig;
            }))
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.POST, "/token/exchange", "/token/refresh", "/token/logout").permitAll()
                    .anyRequest().denyAll()
            );

        return http.build();
    }
}
