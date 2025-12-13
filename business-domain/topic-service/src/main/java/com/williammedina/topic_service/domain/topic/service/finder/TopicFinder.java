package com.williammedina.topic_service.domain.topic.service.finder;

import com.williammedina.topic_service.domain.topic.entity.TopicEntity;

public interface TopicFinder {

    TopicEntity findTopicById(Long topicId);

}
