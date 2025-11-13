package com.williammedina.reply_service.infrastructure.client;


import com.williammedina.reply_service.domain.reply.dto.ContentValidationRequest;
import com.williammedina.reply_service.domain.reply.dto.ContentValidationResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ContentValidationClient {

    private final WebClient webClient;

    public ContentValidationClient(WebClient contentValidationClient) {
        this.webClient = contentValidationClient;
    }

    public Mono<ContentValidationResponse> validateContent(String text) {
        return webClient.post()
                .uri("/content")
                .bodyValue(new ContentValidationRequest(text))
                .retrieve()
                .bodyToMono(ContentValidationResponse.class);
    }

}
