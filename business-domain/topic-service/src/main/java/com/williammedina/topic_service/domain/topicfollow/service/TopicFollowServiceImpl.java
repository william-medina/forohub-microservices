package com.williammedina.topic_service.domain.topicfollow.service;

import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import com.williammedina.topic_service.domain.topic.repository.TopicRepository;
import com.williammedina.topic_service.domain.topic.dto.*;
import com.williammedina.topic_service.domain.topicfollow.dto.TopicFollowDetailsDTO;
import com.williammedina.topic_service.domain.topicfollow.dto.TopicFollowerDTO;
import com.williammedina.topic_service.domain.topicfollow.dto.UserFollowedTopicCountDTO;
import com.williammedina.topic_service.domain.topicfollow.entity.TopicFollowEntity;
import com.williammedina.topic_service.domain.topicfollow.repository.TopicFollowRepository;
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
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TopicFollowServiceImpl implements TopicFollowService, InternalTopicFollowService {

    private final TopicFollowRepository topicFollowRepository;
    private final UserServiceClient userServiceClient;
    private final CourseServiceClient courseServiceClient;
    private final ReplyServiceClient replyServiceClient;
    private final TopicRepository topicRepository;
    private final TopicFollowEventPublisher topicFollowEventPublisher;

    @Override
    @Transactional
    public TopicFollowDetailsDTO toggleFollowTopic(Long userId, Long topicId) {
        UserDTO user = userServiceClient.getUserById(userId);
        TopicEntity topic = findTopicById(topicId);
        CourseDTO course = courseServiceClient.getCourseById(topic.getCourseId());
        ReplyCountDTO count = replyServiceClient.getReplyCountByTopic(topicId);

        if (user.id().equals(topic.getUserId())) {
            log.warn("User ID: {} attempted to follow their own topic with ID {}", user.id(), topicId);
            throw new AppException("No puedes seguir un tópico que has creado." , HttpStatus.CONFLICT);
        }

        boolean isFollowing = topicFollowRepository.existsByUserIdAndTopicIdAndTopicIsDeletedFalse(user.id(), topic.getId());

        if (isFollowing) {
            TopicFollowEntity followToDelete = findTopicFollowByUserIdAndTopic(user.id(), topic);
            topicFollowRepository.deleteByUserIdAndTopicId(user.id(), topicId);
            log.info("User ID: {} unfollowed topic with ID {}", user.id(), topicId);

            TopicFollowPayload topicFollowPayload = new TopicFollowPayload(new TopicFollowerDTO(user ,followToDelete.getFollowedAt()), followToDelete.getTopic().getId());
            topicFollowEventPublisher.publishEvent(TopicFollowEventType.UNFOLLOW, topicFollowPayload);
            return new TopicFollowDetailsDTO(TopicDTO.fromEntity(topic, user, course, count.count()), null);
        } else {
            TopicFollowEntity newFollow = topicFollowRepository.save(new TopicFollowEntity(user.id(), topic));
            log.info("User ID: {} followed topic with ID {}", user.id(), topicId);

            TopicFollowPayload topicFollowPayload = new TopicFollowPayload(new TopicFollowerDTO(user ,newFollow.getFollowedAt()), newFollow.getTopic().getId());
            topicFollowEventPublisher.publishEvent(TopicFollowEventType.FOLLOW, topicFollowPayload);
            return new TopicFollowDetailsDTO(TopicDTO.fromEntity(newFollow.getTopic(), user, course, count.count()), newFollow.getFollowedAt());
        }

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

        List<TopicFollowDetailsDTO> topicFollowerDTOs = mapTopicFollowsDetailsToDTOs(topicFollowsPage.getContent());
        return new PageImpl<>(topicFollowerDTOs, pageable, topicFollowsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public UserFollowedTopicCountDTO getFollowedTopicCountByUser(Long userId) {
        Long count = topicFollowRepository.countByUserIdAndTopicIsDeletedFalse(userId);
        return new UserFollowedTopicCountDTO(count);
    }

    public List<TopicFollowDetailsDTO> mapTopicFollowsDetailsToDTOs(List<TopicFollowEntity> topicFollows) {

        List<Long> userIds = topicFollows.stream()
                .map(TopicFollowEntity::getUserId)
                .distinct()
                .toList();

        List<Long> courseIds = topicFollows.stream()
                .map(topicFollow -> topicFollow.getTopic().getCourseId())
                .distinct()
                .toList();

        List<UserDTO> users = userServiceClient.getUsersByIds(userIds);
        List<CourseDTO> courses = courseServiceClient.getCoursesByIds(courseIds);
        List<ReplyCountDTO> repliesCount = replyServiceClient.getReplyCountsByTopicIds(topicFollows.stream().map(t -> t.getTopic().getId()).toList());

        Map<Long, UserDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserDTO::id, u -> u));

        Map<Long, CourseDTO> courseMap = courses.stream()
                .collect(Collectors.toMap(CourseDTO::id, c -> c));

        return topicFollows.stream()
                .map(topicFollow -> {
                    UserDTO user = userMap.get(topicFollow.getUserId());
                    CourseDTO course = courseMap.get(topicFollow.getTopic().getCourseId());
                    Long count = repliesCount.stream()
                            .filter(r -> r.topicId().equals(topicFollow.getId()))
                            .findFirst()
                            .map(ReplyCountDTO::count)
                            .orElse(0L);
                    return toTopicFollowDetailsDTO(topicFollow, user, course, count);
                })
                .toList();
    }

    public List<TopicFollowerDTO> mapTopicFollowerToDTOs(List<TopicFollowEntity> topicFollows) {

        List<Long> userIds = topicFollows.stream()
                .map(TopicFollowEntity::getUserId)
                .distinct()
                .toList();

        List<UserDTO> users = userServiceClient.getUsersByIds(userIds);

        Map<Long, UserDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserDTO::id, u -> u));

        return topicFollows.stream()
                .map(topicFollow -> {
                    UserDTO user = userMap.get(topicFollow.getUserId());
                    return toTopicFollowDTO(topicFollow, user);
                })
                .toList();
    }

    private TopicFollowDetailsDTO toTopicFollowDetailsDTO(TopicFollowEntity topicFollow, UserDTO user, CourseDTO course, Long repliesCount) {
        return new TopicFollowDetailsDTO(TopicDTO.fromEntity(topicFollow.getTopic(), user, course, repliesCount), topicFollow.getFollowedAt());
    }

    private TopicFollowerDTO toTopicFollowDTO(TopicFollowEntity topicFollow, UserDTO user) {
        return new TopicFollowerDTO(user, topicFollow.getFollowedAt());
    }

    public TopicEntity findTopicById(Long topicId) {
        return topicRepository.findByIdAndIsDeletedFalse(topicId)
                .orElseThrow(() -> {
                    log.warn("Topic not found with ID: {}", topicId);
                    return new AppException("Tópico no encontrado", HttpStatus.NOT_FOUND);
                });
    }

    public TopicFollowEntity findTopicFollowByUserIdAndTopic(Long userId, TopicEntity topic) {
        return topicFollowRepository.findByUserIdAndTopic(userId, topic)
                .orElseThrow(() -> {
                    log.warn("Topic follow not found with user ID: {}, topic Id: {}", userId, topic.getId());
                    return new AppException("Seguidor no encontrado", HttpStatus.NOT_FOUND);
                });
    }

}
