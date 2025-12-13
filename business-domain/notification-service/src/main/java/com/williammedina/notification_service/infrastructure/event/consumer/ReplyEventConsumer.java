package com.williammedina.notification_service.infrastructure.event.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.notification_service.domain.notification.service.handler.DomainEventHandler;
import com.williammedina.notification_service.infrastructure.event.model.reply.ReplyEvent;
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
public class ReplyEventConsumer {

    private final DomainEventHandler<ReplyEvent> replyEventHandler;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "reply-events",
            groupId = "notification-service-reply-group"
    )
    public void consume(String message, Acknowledgment ack) throws JsonProcessingException {

        ReplyEvent event = objectMapper.readValue(message, ReplyEvent.class);
        log.info("Received event from reply-service: {}", event);

        try {

            if (processedEventRepository.existsById(event.eventId())) {
                ack.acknowledge();
                return;
            }

            replyEventHandler.handle(event);

            processedEventRepository.save(new ProcessedEventEntity(
                    event.eventId(),
                    event.eventType().name(),
                    event.sourceService()
            ));

            ack.acknowledge();

        } catch (Exception e) {
            log.error("Error processing event from reply-service: {}", e.getMessage(), e);
        }
    }
}

