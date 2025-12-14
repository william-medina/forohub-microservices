package com.williammedina.topic_service.domain.topic.service.validator;

import com.williammedina.topic_service.domain.topic.dto.ContentValidationResponse;

public interface TopicValidator {

    void ensureTitleIsUnique(String title);
    void ensureDescriptionIsUnique(String description);
    void ensureTitleContentIsValid(ContentValidationResponse validationResponse);
    void ensureDescriptionContentIsValid(ContentValidationResponse validationResponse);

}
