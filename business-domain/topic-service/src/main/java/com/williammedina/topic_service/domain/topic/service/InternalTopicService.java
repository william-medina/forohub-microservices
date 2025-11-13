package com.williammedina.topic_service.domain.topic.service;

import com.williammedina.topic_service.domain.topic.dto.*;
import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.List;


public interface InternalTopicService {

    TopicSummaryDTO getTopicSummaryById(Long topicId);
    TopicDetailsDTO changeTopicStatus(InputTopicStatusDTO data, Long topicId);
    UserTopicCountDTO getTopicCountByUser(Long userId);
    List<TopicDetailsDTO> exportAllTopics();

}
