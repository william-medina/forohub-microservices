package com.williammedina.topic_read_service.domain.topicread.service.finder;

import com.williammedina.topic_read_service.domain.topicread.document.TopicReadDocument;

public interface TopicReadFinder {

    TopicReadDocument findTopicById(Long topicId);

}
