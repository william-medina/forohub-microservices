package com.williammedina.email_service.infrastructure.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.email_service.domain.email.service.EmailService;
import com.williammedina.email_service.domain.email.dto.TopicDetailsDTO;
import com.williammedina.email_service.infrastructure.event.model.topic.TopicEvent;
import com.williammedina.email_service.infrastructure.event.model.topic.TopicPayload;
import com.williammedina.email_service.infrastructure.persistence.processedevent.ProcessedEventEntity;
import com.williammedina.email_service.infrastructure.persistence.processedevent.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicEventConsumer {

    private final EmailService emailService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public Consumer<TopicEvent> topicEvents() {
        return event -> {
            log.info("Received topic-service event: {}", event);

            try {

                if (processedEventRepository.existsById(event.eventId())) {
                    return;
                }

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

                processedEventRepository.save(new ProcessedEventEntity(
                        event.eventId(),
                        event.eventType().name(),
                        event.sourceService()
                ));

            } catch (Exception e) {
                log.error("Error processing topic-service event: {}", e.getMessage(), e);
            }
        };
    }
}
