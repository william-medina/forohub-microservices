package com.williammedina.topic_read_service.domain.topicread.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.topic_read_service.domain.topicread.dto.TopicFollowerDTO;
import com.williammedina.topic_read_service.domain.topicread.model.Follower;
import com.williammedina.topic_read_service.domain.topicread.repository.TopicReadRepository;
import com.williammedina.topic_read_service.infrastructure.event.model.topicfollow.TopicFollowEvent;
import com.williammedina.topic_read_service.infrastructure.event.model.topicfollow.TopicFollowPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicFollowEventHandler implements DomainEventHandler<TopicFollowEvent> {

    private final TopicReadRepository topicReadRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(TopicFollowEvent event) {
        TopicFollowPayload payload = objectMapper.convertValue(event.payload(), TopicFollowPayload.class);
        TopicFollowerDTO topicFollowDto = payload.topicFollower();
        Long topicId = payload.topicId();

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
    }
}
