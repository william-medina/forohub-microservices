package com.williammedina.user_service.infrastructure.client;

import com.williammedina.user_service.domain.user.dto.ContentValidationRequest;
import com.williammedina.user_service.domain.user.dto.ContentValidationResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ContentValidationClient {

    private final WebClient webClient;

    public ContentValidationClient(WebClient contentValidationClient) {
        this.webClient = contentValidationClient;
    }

    public Mono<ContentValidationResponse> validateUsername(String username) {
        return webClient.post()
                .uri("/username")
                .bodyValue(new ContentValidationRequest(username))
                .retrieve()
                .bodyToMono(ContentValidationResponse.class);
    }
}
