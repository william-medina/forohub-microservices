package com.williammedina.topic_read_service.domain.topicread.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.topic_read_service.domain.topicread.document.TopicReadDocument;
import com.williammedina.topic_read_service.domain.topicread.dto.*;
import com.williammedina.topic_read_service.domain.topicread.repository.TopicReadRepository;
import com.williammedina.topic_read_service.infrastructure.event.model.topic.TopicEvent;
import com.williammedina.topic_read_service.infrastructure.event.model.topic.TopicPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicEventHandler implements DomainEventHandler<TopicEvent> {

    private final TopicReadRepository topicReadRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(TopicEvent event) {
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
