package com.williammedina.topic_service.infrastructure.event.publisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.topic_service.infrastructure.event.model.topic.TopicEvent;
import com.williammedina.topic_service.infrastructure.event.model.topic.TopicEventType;
import com.williammedina.topic_service.infrastructure.event.model.topicfollow.TopicFollowEvent;
import com.williammedina.topic_service.infrastructure.event.model.topicfollow.TopicFollowEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class TopicFollowEventPublisher {

    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;

    private static final String APPLICATION_NAME = "topic-follow-service";

    public void publishEvent(TopicFollowEventType eventType, Object dto) {
        JsonNode payload = objectMapper.valueToTree(dto);
        TopicFollowEvent event = new TopicFollowEvent(eventType, APPLICATION_NAME, payload);
        streamBridge.send("topic-follow-events-out-0", event);
        log.debug("Event published: {}", event);
    }
}
