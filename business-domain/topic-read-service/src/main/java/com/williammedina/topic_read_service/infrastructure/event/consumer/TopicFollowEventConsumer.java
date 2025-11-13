package com.williammedina.topic_read_service.infrastructure.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.topic_read_service.domain.topicread.dto.*;
import com.williammedina.topic_read_service.domain.topicread.model.Follower;
import com.williammedina.topic_read_service.domain.topicread.repository.TopicReadRepository;
import com.williammedina.topic_read_service.infrastructure.event.model.topicfollow.TopicFollowEvent;
import com.williammedina.topic_read_service.infrastructure.event.model.topicfollow.TopicFollowPayload;
import com.williammedina.topic_read_service.infrastructure.persistence.processedevent.ProcessedEventDocument;
import com.williammedina.topic_read_service.infrastructure.persistence.processedevent.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicFollowEventConsumer {

    private final ProcessedEventRepository processedEventRepository;
    private final TopicReadRepository topicReadRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public Consumer<TopicFollowEvent> topicFollowEvents() {
        return event -> {
            log.info("Received topic-follow-service event: {}", event);

            try {
                // Evitar reprocesar eventos
                if (processedEventRepository.existsById(event.eventId())) {
                    return;
                }

                // Convertir payload a DTO
                TopicFollowPayload payload = objectMapper.convertValue(event.payload(), TopicFollowPayload.class);
                TopicFollowerDTO topicFollowDto = payload.topicFollower();
                Long topicId = payload.topicId();
                System.out.println(payload + " - " + topicId + " - " + event.eventType());

                topicReadRepository.findById(topicId).ifPresent(topicDoc -> {
                    List<Follower> followers = topicDoc.getFollowers();

                    switch (event.eventType()) {
                        case FOLLOW -> {
                            followers.add(TopicFollowerDTO.fromDto(topicFollowDto));
                            topicDoc.setFollowers(followers);
                            topicReadRepository.save(topicDoc);
                            log.info("Added follow to topic {}", topicId);
                        }
                        case UNFOLLOW -> {
                            followers.removeIf(f -> f.getUser().getId().equals(topicFollowDto.user().id()));
                            topicDoc.setFollowers(followers);
                            topicReadRepository.save(topicDoc);
                            log.info("Deleted follow from topic {}", topicId);
                        }
                    }
                });
                // Marcar evento como procesado
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
