package com.williammedina.topic_service.domain.topic.service.validator;

import com.williammedina.topic_service.domain.topic.dto.ContentValidationResponse;
import com.williammedina.topic_service.domain.topic.repository.TopicRepository;
import com.williammedina.topic_service.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicValidatorImpl implements TopicValidator {

    private final TopicRepository topicRepository;

    @Override
    public void existsByTitle(String title) {
        if (topicRepository.existsByTitleAndIsDeletedFalse(title)) {
            log.warn("A topic already exists with title: {}", title);
            throw new AppException("El titulo ya existe.", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void existsByDescription(String description) {
        if (topicRepository.existsByDescriptionAndIsDeletedFalse(description)) {
            log.warn("A topic already exists with description: {}", description);
            throw new AppException("La descripción ya existe.", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void validateTitleContent(ContentValidationResponse validationResponse) {
        if (!"approved".equals(validationResponse.result())) {
            log.warn("Title content not approved: {}", validationResponse.result());
            throw new AppException("El título " + validationResponse.result(), HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void validateDescriptionContent(ContentValidationResponse validationResponse) {
        if (!"approved".equals(validationResponse.result())) {
            log.warn("Description content not approved: {}", validationResponse.result());
            throw new AppException("La descripción " + validationResponse.result(), HttpStatus.FORBIDDEN);
        }
    }
}
