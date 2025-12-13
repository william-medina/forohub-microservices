package com.williammedina.topic_service.domain.topicfollow.service;

import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import com.williammedina.topic_service.domain.topic.dto.*;
import com.williammedina.topic_service.domain.topicfollow.dto.TopicFollowDetailsDTO;
import com.williammedina.topic_service.domain.topicfollow.dto.TopicFollowerDTO;
import com.williammedina.topic_service.domain.topicfollow.entity.TopicFollowEntity;
import com.williammedina.topic_service.domain.topicfollow.repository.TopicFollowRepository;
import com.williammedina.topic_service.domain.topicfollow.service.finder.TopicFollowFinder;
import com.williammedina.topic_service.domain.topicfollow.service.query.TopicFollowQueryMapper;
import com.williammedina.topic_service.infrastructure.client.CourseServiceClient;
import com.williammedina.topic_service.infrastructure.client.ReplyServiceClient;
import com.williammedina.topic_service.infrastructure.client.UserServiceClient;
import com.williammedina.topic_service.infrastructure.event.model.topicfollow.TopicFollowEventType;
import com.williammedina.topic_service.infrastructure.event.model.topicfollow.TopicFollowPayload;
import com.williammedina.topic_service.infrastructure.event.publisher.TopicFollowEventPublisher;
import com.williammedina.topic_service.infrastructure.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TopicFollowServiceImpl implements TopicFollowService {

    private final TopicFollowRepository topicFollowRepository;
    private final UserServiceClient userServiceClient;
    private final CourseServiceClient courseServiceClient;
    private final ReplyServiceClient replyServiceClient;
    private final TopicFollowEventPublisher topicFollowEventPublisher;
    private final TopicFollowQueryMapper topicFollowQueryMapper;
    private final TopicFollowFinder topicFollowFinder;

    @Override
    @Transactional
    public TopicFollowDetailsDTO toggleFollowTopic(Long userId, Long topicId) {
        UserDTO currentUser = userServiceClient.getUserById(userId);
        TopicEntity topic = topicFollowFinder.findTopicById(topicId);
        CourseDTO course = courseServiceClient.getCourseById(topic.getCourseId());
        ReplyCountDTO count = replyServiceClient.getReplyCountByTopic(topicId);

        if (currentUser.id().equals(topic.getUserId())) {
            log.warn("User ID: {} attempted to follow their own topic with ID {}", currentUser.id(), topicId);
            throw new AppException("No puedes seguir un tópico que has creado." , HttpStatus.CONFLICT);
        }

        boolean isFollowing = topicFollowRepository.existsByUserIdAndTopicIdAndTopicIsDeletedFalse(currentUser.id(), topic.getId());

        if (isFollowing) {
            TopicFollowEntity followToDelete = topicFollowFinder.findTopicFollowByUserIdAndTopic(currentUser.id(), topic);
            topicFollowRepository.deleteByUserIdAndTopicId(currentUser.id(), topicId);
            log.info("User ID: {} unfollowed topic with ID {}", currentUser.id(), topicId);

            TopicFollowPayload topicFollowPayload = new TopicFollowPayload(new TopicFollowerDTO(currentUser ,followToDelete.getFollowedAt()), followToDelete.getTopic().getId());
            topicFollowEventPublisher.publishEvent(TopicFollowEventType.UNFOLLOW, topicFollowPayload);
            return new TopicFollowDetailsDTO(TopicDTO.fromEntity(topic, currentUser, course, count.count()), null);
        }

        TopicFollowEntity newFollow = topicFollowRepository.save(new TopicFollowEntity(currentUser.id(), topic));
        log.info("User ID: {} followed topic with ID {}", currentUser.id(), topicId);

        TopicFollowPayload topicFollowPayload = new TopicFollowPayload(new TopicFollowerDTO(currentUser ,newFollow.getFollowedAt()), newFollow.getTopic().getId());
        topicFollowEventPublisher.publishEvent(TopicFollowEventType.FOLLOW, topicFollowPayload);
        return new TopicFollowDetailsDTO(TopicDTO.fromEntity(newFollow.getTopic(), currentUser, course, count.count()), newFollow.getFollowedAt());

    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicFollowDetailsDTO> getFollowedTopicsByUser(Long userId, Pageable pageable, String keyword) {
        Page<TopicFollowEntity> topicFollowsPage;
        UserDTO user = userServiceClient.getUserById(userId);
        log.debug("Fetching followed topics for user ID: {}", user.id());

        if (keyword != null ) {
            topicFollowsPage = topicFollowRepository.findByUserIdWithKeyword(user.id(), keyword, pageable);
        } else {
            topicFollowsPage = topicFollowRepository.findByUserIdSorted(user.id(), pageable);
        }

        List<TopicFollowDetailsDTO> topicFollowerDTOs = topicFollowQueryMapper.mapTopicFollowsDetailsToDTOs(topicFollowsPage.getContent());
        return new PageImpl<>(topicFollowerDTOs, pageable, topicFollowsPage.getTotalElements());
    }

}
