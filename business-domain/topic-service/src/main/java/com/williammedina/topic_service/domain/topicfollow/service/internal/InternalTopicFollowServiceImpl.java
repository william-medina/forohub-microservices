package com.williammedina.topic_service.domain.topicfollow.service.internal;

import com.williammedina.topic_service.domain.topicfollow.dto.UserFollowedTopicCountDTO;
import com.williammedina.topic_service.domain.topicfollow.repository.TopicFollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalTopicFollowServiceImpl implements InternalTopicFollowService {

    private final TopicFollowRepository topicFollowRepository;

    @Override
    @Transactional(readOnly = true)
    public UserFollowedTopicCountDTO getFollowedTopicCountByUser(Long userId) {
        Long count = topicFollowRepository.countByUserIdAndTopicIsDeletedFalse(userId);
        return new UserFollowedTopicCountDTO(count);
    }
}
