package com.williammedina.topic_service.domain.topicfollow.service.finder;

import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import com.williammedina.topic_service.domain.topic.repository.TopicRepository;
import com.williammedina.topic_service.domain.topicfollow.entity.TopicFollowEntity;
import com.williammedina.topic_service.domain.topicfollow.repository.TopicFollowRepository;
import com.williammedina.topic_service.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicFollowFinderImpl implements TopicFollowFinder {

    private final TopicRepository topicRepository;
    private final TopicFollowRepository topicFollowRepository;

    @Override
    public TopicEntity findTopicById(Long topicId) {
        return topicRepository.findByIdAndIsDeletedFalse(topicId)
                .orElseThrow(() -> {
                    log.warn("Topic not found with ID: {}", topicId);
                    return new AppException("Tópico no encontrado", HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public TopicFollowEntity findTopicFollowByUserIdAndTopic(Long userId, TopicEntity topic) {
        return topicFollowRepository.findByUserIdAndTopic(userId, topic)
                .orElseThrow(() -> {
                    log.warn("Topic follow not found with user ID: {}, topic Id: {}", userId, topic.getId());
                    return new AppException("Seguidor no encontrado", HttpStatus.NOT_FOUND);
                });
    }
}
