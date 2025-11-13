package com.williammedina.notification_service.infrastructure.event.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.notification_service.domain.notification.service.NotificationService;
import com.williammedina.notification_service.domain.notification.dto.TopicDetailsDTO;
import com.williammedina.notification_service.infrastructure.event.model.topic.TopicEvent;
import com.williammedina.notification_service.infrastructure.event.model.topic.TopicPayload;
import com.williammedina.notification_service.infrastructure.persistence.processedevent.ProcessedEventEntity;
import com.williammedina.notification_service.infrastructure.persistence.processedevent.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicEventConsumer {

    private final NotificationService notificationService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "topic-events",
            groupId = "notification-service-topic-group"
    )
    public void consume(String message, Acknowledgment ack) throws JsonProcessingException {

        TopicEvent event = objectMapper.readValue(message, TopicEvent.class);
        log.info("Received event from topic-service: {}", event);

        try {

            if (processedEventRepository.existsById(event.eventId())) {
                ack.acknowledge();
                return;
            }

            TopicPayload payload = objectMapper.convertValue(event.payload(), TopicPayload.class);
            TopicDetailsDTO topic = payload.topic();
            Long userId = payload.userId();

            boolean isNotTopicAuthor = !userId.equals(topic.author().id());

            switch (event.eventType()) {
                case UPDATED -> {
                    if(isNotTopicAuthor) {
                        log.info("Notifying topic owner ID: {} about update", topic.id());
                        notificationService.notifyTopicEdited(topic);
                    }
                }
                case STATUS_CHANGED -> {
                    if (topic.status().equals(TopicDetailsDTO.Status.CLOSED)) {
                        log.info("Sending notifications for topic marked as solved");
                        notificationService.notifyTopicSolved(topic);
                        notificationService.notifyFollowersTopicSolved(topic);
                    }
                }
                case DELETED -> {
                    if(isNotTopicAuthor) {
                        log.info("Notifying topic owner ID: {} about deletion", topic.id());
                        notificationService.notifyTopicDeleted(topic);
                    }
                }
            }

            processedEventRepository.save(new ProcessedEventEntity(
                    event.eventId(),
                    event.eventType().name(),
                    event.sourceService()
            ));

            ack.acknowledge();

        } catch (Exception e) {
            log.error("Error processing event from topic-service: {}", e.getMessage(), e);
        }
    }
}
