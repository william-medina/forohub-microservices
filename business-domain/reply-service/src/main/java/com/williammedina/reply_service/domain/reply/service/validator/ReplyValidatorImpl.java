package com.williammedina.reply_service.domain.reply.service.validator;

import com.williammedina.reply_service.domain.reply.dto.ContentValidationResponse;
import com.williammedina.reply_service.domain.reply.dto.TopicSummaryDTO;
import com.williammedina.reply_service.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyValidatorImpl implements ReplyValidator {

    @Override
    public void ensureReplyContentIsValid(ContentValidationResponse validationResponse) {
        if (!"approved".equals(validationResponse.result())) {
            log.warn("Reply content not approved: {}", validationResponse.result());
            throw new AppException("La respuesta " + validationResponse.result(), HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void ensureTopicIsOpen(TopicSummaryDTO topic) {
        if(topic.status().equals(TopicSummaryDTO.Status.CLOSED)) {
            log.warn("Attempted reply to closed topic - ID: {}", topic.id());
            throw new AppException("No se puede crear una respuesta. El tópico está cerrado.", HttpStatus.FORBIDDEN);
        }
    }

}
