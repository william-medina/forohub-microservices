package com.williammedina.topic_read_service.domain.topicread.service;

import com.williammedina.topic_read_service.domain.topicread.document.TopicReadDocument;
import com.williammedina.topic_read_service.domain.topicread.dto.TopicDTO;
import com.williammedina.topic_read_service.domain.topicread.dto.TopicDetailsDTO;
import com.williammedina.topic_read_service.domain.topicread.dto.TopicFollowDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TopicReadService {

    Page<TopicDTO> getAllTopics(Pageable pageable, Long courseId, String keyword, TopicReadDocument.Status status);
    Page<TopicDTO> getAllTopicsByUser(Long userId, Pageable pageable, String keyword);
    TopicDetailsDTO getTopicById(Long topicId);
    Page<TopicFollowDetailsDTO> getFollowedTopicsByUser(Long userId, Pageable pageable, String keyword);

}
