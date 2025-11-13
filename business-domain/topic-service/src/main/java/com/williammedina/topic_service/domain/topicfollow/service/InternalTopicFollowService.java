package com.williammedina.topic_service.domain.topicfollow.service;

import com.williammedina.topic_service.domain.topicfollow.dto.UserFollowedTopicCountDTO;

public interface InternalTopicFollowService {

    UserFollowedTopicCountDTO getFollowedTopicCountByUser(Long userId);

}
