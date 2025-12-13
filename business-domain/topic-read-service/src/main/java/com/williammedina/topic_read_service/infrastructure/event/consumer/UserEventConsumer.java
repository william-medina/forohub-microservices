package com.williammedina.topic_read_service.infrastructure.event.consumer;


import com.williammedina.topic_read_service.domain.topicread.service.handler.DomainEventHandler;
import com.williammedina.topic_read_service.infrastructure.event.model.user.UserEvent;
import com.williammedina.topic_read_service.infrastructure.persistence.processedevent.ProcessedEventDocument;
import com.williammedina.topic_read_service.infrastructure.persistence.processedevent.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final DomainEventHandler<UserEvent> userEventHandler;
    private final ProcessedEventRepository processedEventRepository;

    @Bean
    public Consumer<UserEvent> userEvents() {
        return event -> {
            log.info("Received event from user-service: {}", event);

            try {

                if (processedEventRepository.existsById(event.eventId())) {
                    return;
                }

                userEventHandler.handle(event);

                processedEventRepository.save(new ProcessedEventDocument(
                        event.eventId(),
                        event.eventType().name(),
                        event.sourceService()
                ));

            } catch (Exception e) {
                log.error("Error processing event from user-service: {}", e.getMessage(), e);
            }
        };
    }
}

