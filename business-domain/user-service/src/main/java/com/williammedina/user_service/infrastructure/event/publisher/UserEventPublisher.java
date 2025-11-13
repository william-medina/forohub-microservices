package com.williammedina.user_service.infrastructure.event.publisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.user_service.infrastructure.event.model.UserEventType;
import com.williammedina.user_service.infrastructure.event.model.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String applicationName;

    public void publishEvent(UserEventType eventType, Object dto) {
        JsonNode payload = objectMapper.valueToTree(dto);
        UserEvent event = new UserEvent(eventType, applicationName, payload);
        streamBridge.send("user-events-out-0", event);
        log.debug("Event published: {}", event);
    }
}
