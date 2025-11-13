package com.williammedina.content_analysis_service.domain.contentvalidation.service;

import com.williammedina.content_analysis_service.domain.contentvalidation.dto.ContentValidationResponse;
import reactor.core.publisher.Mono;

public interface ContentValidationService {

    Mono<ContentValidationResponse> validateContent(String content);
    Mono<ContentValidationResponse> validateUsername(String username);

}
