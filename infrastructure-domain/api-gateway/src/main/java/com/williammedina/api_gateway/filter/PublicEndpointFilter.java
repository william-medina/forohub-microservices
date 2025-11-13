package com.williammedina.api_gateway.filter;

import com.williammedina.api_gateway.filter.model.PublicEndpoint;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PublicEndpointFilter implements WebFilter, Ordered {

    public static final List<PublicEndpoint> PUBLIC_ENDPOINTS = List.of(
            new PublicEndpoint("/api/auth/create-account", HttpMethod.POST),
            new PublicEndpoint("/api/auth/request-code", HttpMethod.POST),
            new PublicEndpoint("/api/auth/forgot-password", HttpMethod.POST),
            new PublicEndpoint("/api/auth/update-password/{token}", HttpMethod.POST),
            new PublicEndpoint("/api/auth/confirm-account/{token}", HttpMethod.GET),
            new PublicEndpoint("/api/topic", HttpMethod.GET),
            new PublicEndpoint("/api/topic/{topicId}", HttpMethod.GET),
            new PublicEndpoint("/api/reply/{replyId}", HttpMethod.GET),
            new PublicEndpoint("/api/course", HttpMethod.GET)
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        boolean isPublic = PUBLIC_ENDPOINTS.stream().anyMatch(ep ->
                path.matches(ep.url()
                        .replace("{token}", "[^/]+")
                        .replace("{topicId}", "[^/]+")
                        .replace("{replyId}", "[^/]+")
                        .replace("**", ".*"))
                        && ep.method().name().equals(method)
        );

        if (isPublic) {
            // Eliminar cabecera Authorization para que Spring Security no intente validar el JWT
            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .headers(httpHeaders -> httpHeaders.remove(HttpHeaders.AUTHORIZATION))
                    .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Ejecutar antes que el filtro de seguridad de Spring
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
