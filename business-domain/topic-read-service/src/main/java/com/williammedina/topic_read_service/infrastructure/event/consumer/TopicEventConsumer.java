package com.williammedina.topic_read_service.infrastructure.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.topic_read_service.domain.topicread.document.TopicReadDocument;
import com.williammedina.topic_read_service.domain.topicread.dto.*;
import com.williammedina.topic_read_service.domain.topicread.repository.TopicReadRepository;
import com.williammedina.topic_read_service.infrastructure.event.model.topic.TopicEvent;
import com.williammedina.topic_read_service.infrastructure.event.model.topic.TopicPayload;
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
public class TopicEventConsumer {

    private final ProcessedEventRepository processedEventRepository;
    private final TopicReadRepository topicReadRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public Consumer<TopicEvent> topicEvents() {
        return event -> {
            log.info("Received topic-service event: {}", event);

            try {
                // Evitar reprocesar eventos
                if (processedEventRepository.existsById(event.eventId())) {
                    return;
                }

                // Convertir payload a DTO
                TopicPayload payload = objectMapper.convertValue(event.payload(), TopicPayload.class);
                TopicDetailsDTO topicDto = payload.topic();

                switch (event.eventType()) {
                    case CREATED -> {
                        TopicReadDocument topicDoc = TopicDetailsDTO.fromDto(topicDto);
                        topicReadRepository.save(topicDoc);
                        log.info("Topic created: {}", topicDoc.getId());
                    }
                    case UPDATED, STATUS_CHANGED -> {
                        topicReadRepository.findById(topicDto.id())
                                .ifPresent(existing -> {
                                    updateDocument(existing, topicDto);
                                    topicReadRepository.save(existing);
                                    log.info("Topic updated: {}", existing.getId());
                                });
                    }
                    case DELETED -> {
                        topicReadRepository.deleteById(topicDto.id());
                        log.info("Topic deleted: {}", topicDto.id());
                    }
                }

                // Marcar evento como procesado
                processedEventRepository.save(new ProcessedEventDocument(
                        event.eventId(),
                        event.eventType().name(),
                        event.sourceService()
                ));

            } catch (Exception e) {
                log.error("Error processing topic-service event: {}", e.getMessage(), e);
            }
        };
    }

    private void updateDocument(TopicReadDocument existing, TopicDetailsDTO dto) {
        existing.setTitle(dto.title());
        existing.setDescription(dto.description());
        existing.setCourse(CourseDTO.fromDto(dto.course()));
        existing.setAuthor(UserDTO.fromDto(dto.author()));
        existing.setReplies(dto.replies().stream().map(ReplyDTO::fromDto).toList());
        existing.setFollowers(dto.followers().stream().map(TopicFollowerDTO::fromDto).toList());
        existing.setStatus(dto.status());
        existing.setUpdatedAt(dto.updatedAt());
    }
}
