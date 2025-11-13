package com.williammedina.reply_service.infrastructure.event.publisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.reply_service.infrastructure.event.model.ReplyEventType;
import com.williammedina.reply_service.infrastructure.event.model.ReplyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReplyEventPublisher {

    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String applicationName;

    public void publishEvent(ReplyEventType eventType, Object dto) {
        JsonNode payload = objectMapper.valueToTree(dto);
        ReplyEvent event = new ReplyEvent(eventType, applicationName, payload);
        streamBridge.send("reply-events-out-0", event);
        log.debug("Event published: {}", event);
    }
}
