package com.williammedina.topic_service.domain.topic.service.internal;

import com.williammedina.topic_service.domain.topic.dto.*;
import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import com.williammedina.topic_service.domain.topic.repository.TopicRepository;
import com.williammedina.topic_service.domain.topic.service.finder.TopicFinder;
import com.williammedina.topic_service.domain.topic.service.permission.TopicPermissionService;
import com.williammedina.topic_service.domain.topic.service.validator.TopicValidator;
import com.williammedina.topic_service.domain.topicfollow.dto.TopicFollowerDTO;
import com.williammedina.topic_service.domain.topicfollow.service.query.TopicFollowQueryMapper;
import com.williammedina.topic_service.infrastructure.client.CourseServiceClient;
import com.williammedina.topic_service.infrastructure.client.ReplyServiceClient;
import com.williammedina.topic_service.infrastructure.event.model.topic.TopicEventType;
import com.williammedina.topic_service.infrastructure.event.model.topic.TopicPayload;
import com.williammedina.topic_service.infrastructure.event.publisher.TopicEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalTopicServiceImpl implements InternalTopicService {

    private final TopicRepository topicRepository;
    private final CourseServiceClient courseServiceClient;
    private final ReplyServiceClient replyServiceClient;
    private final TopicEventPublisher topicEventPublisher;
    private final TopicValidator validator;
    private final TopicFinder topicFinder;
    private final TopicPermissionService topicPermissionService;
    private final TopicFollowQueryMapper topicFollowQueryMapper;

    @Override
    @Transactional(readOnly = true)
    public TopicSummaryDTO getTopicSummaryById(Long topicId) {
        log.info("Fetching topic summary with ID: {}", topicId);
        TopicEntity topic = topicFinder.findTopicById(topicId);
        UserDTO user = topicPermissionService.getCurrentUser(topic.getUserId());

        return TopicSummaryDTO.fromEntity(topic, user);
    }

    @Override
    @Transactional
    public TopicDetailsDTO changeTopicStatus(InputTopicStatusDTO topicRequest, Long topicId) {
        UserDTO actingUser = topicPermissionService.getCurrentUser(topicRequest.userId());
        TopicEntity topic = topicFinder.findTopicById(topicId);
        CourseDTO course = courseServiceClient.getCourseById(topic.getCourseId());
        topicPermissionService.checkCanModify(topicRequest.userId(), topic);
        topic.setStatus(topicRequest.status());
        log.info("Topic status changed with ID: {}, by user ID: {}", topic.getId(), actingUser.id());

        List<ReplyDTO> replies = replyServiceClient.getAllRepliesByTopic(topic.getId());
        UserDTO author = topicPermissionService.getCurrentUser(topic.getUserId());
        List<TopicFollowerDTO> followers = topicFollowQueryMapper.mapTopicFollowerToDTOs(topic.getFollowedTopics());

        TopicDetailsDTO topicDetailsDTO = TopicDetailsDTO.fromEntity(topic, replies, author, course, followers);
        TopicPayload topicPayload = new TopicPayload(topicDetailsDTO, actingUser.id());
        topicEventPublisher.publishEvent(TopicEventType.STATUS_CHANGED, topicPayload);
        return topicDetailsDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public UserTopicCountDTO getTopicCountByUser(Long userId) {
        Long count = topicRepository.countByUserIdAndIsDeletedFalse(userId);
        return new UserTopicCountDTO(count);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicDetailsDTO> exportAllTopics() {
        log.info("Exporting all topics for initial sync...");

        List<TopicEntity> topics = topicRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc();

        List<TopicDetailsDTO> result = new ArrayList<>();

        for (TopicEntity topic : topics) {
            try {
                UserDTO user = topicPermissionService.getCurrentUser(topic.getUserId());
                CourseDTO course = courseServiceClient.getCourseById(topic.getCourseId());
                List<ReplyDTO> replies = replyServiceClient.getAllRepliesByTopic(topic.getId());
                List<TopicFollowerDTO> followers = topicFollowQueryMapper.mapTopicFollowerToDTOs(topic.getFollowedTopics());

                TopicDetailsDTO dto = TopicDetailsDTO.fromEntity(topic, replies, user, course, followers);
                result.add(dto);
            } catch (Exception e) {
                log.error("Error exporting topic {}: {}", topic.getId(), e.getMessage());
            }
        }

        log.info("Export completed: {} topics exported.", result.size());
        return result;
    }
}
