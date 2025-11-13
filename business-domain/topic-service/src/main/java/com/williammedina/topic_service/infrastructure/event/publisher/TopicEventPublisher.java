package com.williammedina.topic_service.infrastructure.event.publisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.topic_service.infrastructure.event.model.topic.TopicEventType;
import com.williammedina.topic_service.infrastructure.event.model.topic.TopicEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicEventPublisher {

    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String applicationName;

    public void publishEvent(TopicEventType eventType, Object dto) {
        JsonNode payload = objectMapper.valueToTree(dto);
        TopicEvent event = new TopicEvent(eventType, applicationName, payload);
        streamBridge.send("topic-events-out-0", event);
        log.debug("Event published: {}", event);
    }
}
