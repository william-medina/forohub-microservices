package com.williammedina.topic_read_service.infrastructure.event.consumer;

import com.williammedina.topic_read_service.domain.topicread.dto.*;
import com.williammedina.topic_read_service.domain.topicread.service.handler.DomainEventHandler;
import com.williammedina.topic_read_service.infrastructure.event.model.topicfollow.TopicFollowEvent;
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
public class TopicFollowEventConsumer {

    private final DomainEventHandler<TopicFollowEvent> topicFollowEventHandler;
    private final ProcessedEventRepository processedEventRepository;

    @Bean
    public Consumer<TopicFollowEvent> topicFollowEvents() {
        return event -> {
            log.info("Received topic-follow-service event: {}", event);

            try {
                if (processedEventRepository.existsById(event.eventId())) {
                    return;
                }

                topicFollowEventHandler.handle(event);

                processedEventRepository.save(new ProcessedEventDocument(
                        event.eventId(),
                        event.eventType().name(),
                        event.sourceService()
                ));

            } catch (Exception e) {
                log.error("Error processing topic-follow-service event: {}", e.getMessage(), e);
            }
        };
    }
}
