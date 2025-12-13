package com.williammedina.user_service.domain.user.service.stats;

import com.williammedina.user_service.domain.user.dto.UserStatsDTO;
import com.williammedina.user_service.domain.user.entity.UserEntity;
import com.williammedina.user_service.domain.user.service.finder.UserFinder;
import com.williammedina.user_service.infrastructure.client.ReplyServiceClient;
import com.williammedina.user_service.infrastructure.client.TopicServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {

    private final TopicServiceClient topicServiceClient;
    private final ReplyServiceClient replyServiceClient;
    private final UserFinder userFinder;

    @Override
    @Transactional(readOnly = true)
    public UserStatsDTO getUserStats(Long userId) {
        UserEntity currentUser = userFinder.findUserById(userId);
        log.debug("Fetching stats for user ID: {}", currentUser.getId());

        long topicsCount = topicServiceClient.getTopicCountByUser(currentUser.getId()).count();
        long repliesCount = replyServiceClient.getUserReplyCount(currentUser.getId()).count();
        long followedTopicsCount = topicServiceClient.getUserFollowedTopicCount(currentUser.getId()).count();
        return new UserStatsDTO(topicsCount, repliesCount, followedTopicsCount);
    }

}
