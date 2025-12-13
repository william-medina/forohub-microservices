package com.williammedina.topic_service.domain.topic.service.validator;

import com.williammedina.topic_service.domain.topic.dto.ContentValidationResponse;

public interface TopicValidator {

    void existsByTitle(String title);
    void existsByDescription(String description);
    void validateTitleContent(ContentValidationResponse validationResponse);
    void validateDescriptionContent(ContentValidationResponse validationResponse);

}
