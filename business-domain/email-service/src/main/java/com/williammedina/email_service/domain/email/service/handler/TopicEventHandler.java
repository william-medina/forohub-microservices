package com.williammedina.email_service.domain.email.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.email_service.domain.email.dto.TopicDetailsDTO;
import com.williammedina.email_service.domain.email.service.EmailService;
import com.williammedina.email_service.infrastructure.event.model.topic.TopicEvent;
import com.williammedina.email_service.infrastructure.event.model.topic.TopicPayload;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicEventHandler implements DomainEventHandler<TopicEvent> {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(TopicEvent event) throws MessagingException {
        TopicPayload payload = objectMapper.convertValue(event.payload(), TopicPayload.class);
        TopicDetailsDTO topic = payload.topic();
        Long userId = payload.userId();

        boolean isNotTopicAuthor = !userId.equals(topic.author().id());

        switch (event.eventType()) {
            case UPDATED -> {
                if(isNotTopicAuthor) {
                    log.info("Sending email for topic update to topic owner ID: {}", topic.id());
                    emailService.notifyTopicEdited(topic);
                }
            }
            case STATUS_CHANGED -> {
                if (topic.status().equals(TopicDetailsDTO.Status.CLOSED)) {
                    log.info("Sending email for topic marked as solved");
                    emailService.notifyTopicSolved(topic);
                    emailService.notifyFollowersTopicSolved(topic);
                }
            }
            case DELETED -> {
                if(isNotTopicAuthor) {
                    log.info("Sending email for topic deletion to topic owner ID: {}", topic.id());
                    emailService.notifyTopicDeleted(topic);
                }
            }
        }
    }
}
