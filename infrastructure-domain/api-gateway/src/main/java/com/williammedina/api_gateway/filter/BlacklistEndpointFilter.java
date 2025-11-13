package com.williammedina.api_gateway.filter;

import com.williammedina.api_gateway.filter.model.PublicEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class BlacklistEndpointFilter implements WebFilter, Ordered {

    public static final List<PublicEndpoint> INTERNAL_ENDPOINTS = List.of(
            new PublicEndpoint("/api/validation/content", HttpMethod.POST),
            new PublicEndpoint("/api/validation/username", HttpMethod.POST)
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        String clientIp = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        boolean isBlacklisted =
                path.startsWith("/internal/")
                    || INTERNAL_ENDPOINTS.stream().anyMatch(ep ->
                    path.equals(ep.url()) &&
                            (ep.method() == null || ep.method().name().equals(method))
                );

        if (isBlacklisted) {

            log.warn("Blocked access to blacklisted endpoint: {} {} from IP {}", method, path, clientIp);

            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            String body = "{"
                    + "\"timestamp\":\"" + java.time.Instant.now() + "\","
                    + "\"status\":403,"
                    + "\"error\":\"FORBIDDEN\","
                    + "\"message\":\"Endpoint no accesible desde clientes externos.\","
                    + "\"path\":\"" + exchange.getRequest().getPath() + "\""
                    + "}";

            byte[] bytes = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Ejecutar antes que cualquier filtro de seguridad
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
