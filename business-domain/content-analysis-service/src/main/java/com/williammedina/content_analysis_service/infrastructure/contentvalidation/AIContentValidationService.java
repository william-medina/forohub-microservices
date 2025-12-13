package com.williammedina.content_analysis_service.infrastructure.contentvalidation;

import com.williammedina.content_analysis_service.domain.contentvalidation.dto.ContentValidationResponse;
import com.williammedina.content_analysis_service.domain.contentvalidation.service.ContentValidationService;
import com.williammedina.content_analysis_service.infrastructure.contentvalidation.prompts.AIContentPrompts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@ConditionalOnProperty(value = "ai.enabled", havingValue = "true")
public class AIContentValidationService implements ContentValidationService {

    private final ChatClient chatClient;

    public AIContentValidationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Mono<ContentValidationResponse> validateContent(String content) {
        return callAI(AIContentPrompts.CONTENT_VALIDATION_PROMPT, content, "content");
    }

    @Override
    public Mono<ContentValidationResponse> validateUsername(String username) {
        return callAI(AIContentPrompts.USERNAME_VALIDATION_PROMPT, username, "username");
    }

    private Mono<ContentValidationResponse> callAI(String systemPrompt, String userInput, String type) {
        return Mono.fromCallable(() -> {
                    String aiResponse = chatClient.prompt()
                            .system(systemPrompt)
                            .user(userInput)
                            .call()
                            .content();

                    log.info("AI validation result for {}: {}", type, aiResponse.trim());
                    return new ContentValidationResponse(aiResponse.trim());
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error("Error validating {} with AI", type, e));
    }
}
