package com.williammedina.topic_service.domain.topic.service.internal;

import com.williammedina.topic_service.domain.topic.dto.*;

import java.util.List;


public interface InternalTopicService {

    TopicSummaryDTO getTopicSummaryById(Long topicId);
    TopicDetailsDTO changeTopicStatus(InputTopicStatusDTO data, Long topicId);
    UserTopicCountDTO getTopicCountByUser(Long userId);
    List<TopicDetailsDTO> exportAllTopics();

}
