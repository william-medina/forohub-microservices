package com.williammedina.notification_service.domain.notification.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.notification_service.domain.notification.dto.TopicDetailsDTO;
import com.williammedina.notification_service.domain.notification.service.NotificationService;
import com.williammedina.notification_service.infrastructure.event.model.topic.TopicEvent;
import com.williammedina.notification_service.infrastructure.event.model.topic.TopicPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicEventHandler implements DomainEventHandler<TopicEvent> {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(TopicEvent event) {
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
    }
}
