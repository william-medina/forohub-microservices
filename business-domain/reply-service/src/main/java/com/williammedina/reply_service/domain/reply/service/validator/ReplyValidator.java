package com.williammedina.reply_service.domain.reply.service.validator;

import com.williammedina.reply_service.domain.reply.dto.ContentValidationResponse;
import com.williammedina.reply_service.domain.reply.dto.TopicSummaryDTO;

public interface ReplyValidator {

    void ensureReplyContentIsValid(ContentValidationResponse validationResponse);
    void ensureTopicIsOpen(TopicSummaryDTO topic);

}
