package com.williammedina.topic_service.domain.topicfollow.service;

import com.williammedina.topic_service.domain.topicfollow.dto.TopicFollowDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TopicFollowService {

    TopicFollowDetailsDTO toggleFollowTopic(Long userId, Long topicId);
    Page<TopicFollowDetailsDTO> getFollowedTopicsByUser(Long userId, Pageable pageable, String keyword);

}
