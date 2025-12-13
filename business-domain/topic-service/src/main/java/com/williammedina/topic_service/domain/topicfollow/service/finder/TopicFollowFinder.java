package com.williammedina.topic_service.domain.topicfollow.service.finder;

import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import com.williammedina.topic_service.domain.topicfollow.entity.TopicFollowEntity;

public interface TopicFollowFinder {

    TopicEntity findTopicById(Long topicId);
    TopicFollowEntity findTopicFollowByUserIdAndTopic(Long userId, TopicEntity topic);

}
