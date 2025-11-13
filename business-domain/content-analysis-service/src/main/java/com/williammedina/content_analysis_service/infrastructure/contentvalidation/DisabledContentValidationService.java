package com.williammedina.content_analysis_service.infrastructure.contentvalidation;

import com.williammedina.content_analysis_service.domain.contentvalidation.dto.ContentValidationResponse;
import com.williammedina.content_analysis_service.domain.contentvalidation.service.ContentValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@ConditionalOnProperty(value = "ai.enabled", havingValue = "false", matchIfMissing = true)
public class DisabledContentValidationService implements ContentValidationService {

    @Override
    public Mono<ContentValidationResponse> validateContent(String content) {
        log.info("[DISABLED AI] Content validation skipped. Returning 'approved'. Content: {}", content);
        return Mono.just(new ContentValidationResponse("approved"));
    }

    @Override
    public Mono<ContentValidationResponse> validateUsername(String username) {
        log.info("[DISABLED AI] Username validation skipped. Returning 'approved'. Username: {}", username);
        return Mono.just(new ContentValidationResponse("approved"));
    }
}
