package com.williammedina.topic_service.domain.topic.service;

import com.williammedina.topic_service.domain.topic.dto.*;
import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import com.williammedina.topic_service.domain.topic.repository.TopicRepository;
import com.williammedina.topic_service.domain.topic.service.finder.TopicFinder;
import com.williammedina.topic_service.domain.topic.service.permission.TopicPermissionService;
import com.williammedina.topic_service.domain.topic.service.query.TopicQueryMapper;
import com.williammedina.topic_service.domain.topic.service.validator.TopicValidator;
import com.williammedina.topic_service.domain.topicfollow.dto.TopicFollowerDTO;
import com.williammedina.topic_service.domain.topicfollow.service.query.TopicFollowQueryMapper;
import com.williammedina.topic_service.infrastructure.client.ContentValidationClient;
import com.williammedina.topic_service.infrastructure.client.CourseServiceClient;
import com.williammedina.topic_service.infrastructure.client.ReplyServiceClient;
import com.williammedina.topic_service.infrastructure.event.model.topic.TopicEventType;
import com.williammedina.topic_service.infrastructure.event.model.topic.TopicPayload;
import com.williammedina.topic_service.infrastructure.event.publisher.TopicEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final CourseServiceClient courseServiceClient;
    private final ReplyServiceClient replyServiceClient;
    private final ContentValidationClient contentValidationClient;
    private final TopicEventPublisher topicEventPublisher;
    private final TopicValidator validator;
    private final TopicFinder topicFinder;
    private final TopicPermissionService topicPermissionService;
    private final TopicQueryMapper topicQueryMapper;
    private final TopicFollowQueryMapper topicFollowQueryMapper;

    @Override
    @Transactional
    public Mono<TopicDTO> createTopic(Long userId, InputTopicDTO topicRequest) {
        UserDTO currentUser = topicPermissionService.getCurrentUser(userId);
        CourseDTO course = courseServiceClient.getCourseById(topicRequest.courseId());
        log.info("Creating topic by user ID: {}", currentUser.id());

        validator.ensureTitleIsUnique(topicRequest.title());
        validator.ensureDescriptionIsUnique(topicRequest.description());

        Mono<ContentValidationResponse> titleValidation = contentValidationClient.validateContent(topicRequest.title());
        Mono<ContentValidationResponse> descriptionValidation = contentValidationClient.validateContent(topicRequest.description());

        return Mono.zip(titleValidation, descriptionValidation)
                .publishOn(Schedulers.boundedElastic())
                .map(validationResults -> {
                    ContentValidationResponse titleResult = validationResults.getT1();
                    ContentValidationResponse descResult = validationResults.getT2();

                    // Validate the title and description content using AI
                    validator.ensureTitleContentIsValid(titleResult);
                    validator.ensureDescriptionContentIsValid(descResult);

                    TopicEntity createdTopic = topicRepository.save(new TopicEntity(currentUser.id(), topicRequest.title(), topicRequest.description(), course.id()));

                    TopicDetailsDTO topicDetailsDTO = TopicDetailsDTO.fromEntity(createdTopic, new ArrayList<>(), currentUser, course, new ArrayList<>());
                    TopicPayload topicPayload = new TopicPayload(topicDetailsDTO, currentUser.id());
                    topicEventPublisher.publishEvent(TopicEventType.CREATED, topicPayload);
                    return TopicDTO.fromEntity(createdTopic, currentUser, course, 0L);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getAllTopics(Pageable pageable, Long courseId, String keyword, TopicEntity.Status status) {
        Page<TopicEntity> topicsPage;
        log.debug("Fetching topics - page: {}, size: {}, courseId: {}, keyword: {}, status: {}",
                pageable.getPageNumber(), pageable.getPageSize(), courseId, keyword, status);

        if (courseId != null || keyword != null || status != null) {
            topicsPage = topicRepository.findByFilters(courseId, keyword, status, pageable);
        } else {
            topicsPage = topicRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        }

        List<TopicDTO> topicDTOs = topicQueryMapper.mapTopicsToDTOs(topicsPage.getContent());
        return new PageImpl<>(topicDTOs, pageable, topicsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getAllTopicsByUser(Long userId, Pageable pageable, String keyword) {
        Page<TopicEntity> topicsPage;
        UserDTO user = topicPermissionService.getCurrentUser(userId);
        log.debug("Fetching topics for user ID: {} - keyword: {}", user.id(), keyword);

        if (keyword != null ) {
            topicsPage = topicRepository.findByUserIdWithKeyword(user.id(), keyword, pageable);
        } else {
            topicsPage = topicRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(user.id(), pageable);
        }

        List<TopicDTO> topicDTOs = topicQueryMapper.mapTopicsToDTOs(topicsPage.getContent());
        return new PageImpl<>(topicDTOs, pageable, topicsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public TopicDetailsDTO getTopicById(Long topicId) {
        log.info("Fetching topic details with ID: {}", topicId);
        TopicEntity topic = topicFinder.findTopicById(topicId);

        UserDTO user = topicPermissionService.getCurrentUser(topic.getUserId());
        CourseDTO course = courseServiceClient.getCourseById(topic.getCourseId());

        List<ReplyDTO> replies = replyServiceClient.getAllRepliesByTopic(topic.getId());
        List<TopicFollowerDTO> followers = topicFollowQueryMapper.mapTopicFollowerToDTOs(topic.getFollowedTopics());

        return TopicDetailsDTO.fromEntity(topic, replies, user, course, followers);
    }

    @Override
    @Transactional
    public Mono<TopicDetailsDTO> updateTopic(Long userId, InputTopicDTO topicRequest, Long topicId) {
        TopicEntity topicToUpdate = topicFinder.findTopicById(topicId);
        UserDTO actingUser = topicPermissionService.checkCanModify(userId, topicToUpdate);
        CourseDTO course = courseServiceClient.getCourseById(topicRequest.courseId());
        log.info("Updating topic ID: {} by user ID: {}", topicId, actingUser.id());

        Mono<ContentValidationResponse> titleValidation = Mono.just(new ContentValidationResponse("approved"));
        Mono<ContentValidationResponse> descriptionValidation = Mono.just(new ContentValidationResponse("approved"));

        if (!topicRequest.title().equals(topicToUpdate.getTitle())) {
            validator.ensureTitleIsUnique(topicRequest.title());
            titleValidation = contentValidationClient.validateContent(topicRequest.title());
        }

        if (!topicRequest.description().equals(topicToUpdate.getDescription())) {
            validator.ensureDescriptionIsUnique(topicRequest.description());
            descriptionValidation = contentValidationClient.validateContent(topicRequest.description());
        }

        return Mono.zip(titleValidation, descriptionValidation)
                .publishOn(Schedulers.boundedElastic())
                .map(validationResults -> {

                    ContentValidationResponse titleResult = validationResults.getT1();
                    ContentValidationResponse descResult = validationResults.getT2();

                    // Validate the new title and description content using AI
                    validator.ensureTitleContentIsValid(titleResult);
                    validator.ensureDescriptionContentIsValid(descResult);

                    topicToUpdate.setTitle(topicRequest.title());
                    topicToUpdate.setDescription(topicRequest.description());
                    topicToUpdate.setCourseId(course.id());

                    TopicEntity updatedTopic = topicRepository.save(topicToUpdate);
                    log.info("Topic updated ID: {} by user ID: {}", updatedTopic.getId(), actingUser.id());

                    List<ReplyDTO> replies = replyServiceClient.getAllRepliesByTopic(updatedTopic.getId());
                    UserDTO author = topicPermissionService.getCurrentUser(updatedTopic.getUserId());
                    List<TopicFollowerDTO> followers = topicFollowQueryMapper.mapTopicFollowerToDTOs(updatedTopic.getFollowedTopics());

                    TopicDetailsDTO topicDetailsDTO = TopicDetailsDTO.fromEntity(updatedTopic, replies, author, course, followers);
                    TopicPayload topicPayload = new TopicPayload(topicDetailsDTO, actingUser.id());
                    topicEventPublisher.publishEvent(TopicEventType.UPDATED, topicPayload);
                    return topicDetailsDTO;
                });
    }

    @Override
    @Transactional
    public void deleteTopic(Long userId, Long topicId) {
        TopicEntity topicToDelete = topicFinder.findTopicById(topicId);
        UserDTO actingUser = topicPermissionService.checkCanModify(userId, topicToDelete);
        CourseDTO course = courseServiceClient.getCourseById(topicToDelete.getCourseId());
        topicToDelete.markAsDeleted(); //topicRepository.delete(topic);
        log.info("Topic marked as deleted - ID: {} by user ID: {}", topicId, actingUser.id());

        List<ReplyDTO> replies = replyServiceClient.getAllRepliesByTopic(topicToDelete.getId());
        UserDTO author = topicPermissionService.getCurrentUser(topicToDelete.getUserId());
        List<TopicFollowerDTO> followers = topicFollowQueryMapper.mapTopicFollowerToDTOs(topicToDelete.getFollowedTopics());

        TopicDetailsDTO topicDetailsDTO = TopicDetailsDTO.fromEntity(topicToDelete, replies, author, course, followers);
        TopicPayload topicPayload = new TopicPayload(topicDetailsDTO, actingUser.id());
        topicEventPublisher.publishEvent(TopicEventType.DELETED, topicPayload);
    }

}
