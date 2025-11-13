package com.williammedina.topic_service.domain.topic.service;

import com.williammedina.topic_service.domain.topic.dto.*;
import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;


public interface TopicService {

    Mono<TopicDTO> createTopic(Long userId, InputTopicDTO data);
    Page<TopicDTO> getAllTopics(Pageable pageable, Long courseId, String keyword, TopicEntity.Status status);
    Page<TopicDTO> getAllTopicsByUser(Long userId, Pageable pageable, String keyword);
    TopicDetailsDTO getTopicById(Long topicId);
    Mono<TopicDetailsDTO> updateTopic(Long userId, InputTopicDTO data, Long topicId);
    void deleteTopic(Long userId, Long topicId);

}
