package com.williammedina.topic_service.domain.topic.service;

import com.williammedina.topic_service.domain.topic.dto.*;
import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import com.williammedina.topic_service.domain.topic.repository.TopicRepository;
import com.williammedina.topic_service.domain.topicfollow.service.TopicFollowServiceImpl;
import com.williammedina.topic_service.domain.topicfollow.dto.TopicFollowerDTO;
import com.williammedina.topic_service.infrastructure.client.ContentValidationClient;
import com.williammedina.topic_service.infrastructure.client.CourseServiceClient;
import com.williammedina.topic_service.infrastructure.client.ReplyServiceClient;
import com.williammedina.topic_service.infrastructure.client.UserServiceClient;
import com.williammedina.topic_service.infrastructure.event.model.topic.TopicEventType;
import com.williammedina.topic_service.infrastructure.event.model.topic.TopicPayload;
import com.williammedina.topic_service.infrastructure.event.publisher.TopicEventPublisher;
import com.williammedina.topic_service.infrastructure.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService, InternalTopicService {

    private final TopicRepository topicRepository;
    private final CourseServiceClient courseServiceClient;
    private final UserServiceClient userServiceClient;
    private final ReplyServiceClient replyServiceClient;
    private final ContentValidationClient contentValidationClient;
    private final TopicFollowServiceImpl topicFollowService;
    private final TopicEventPublisher topicEventPublisher;

    @Override
    @Transactional
    public Mono<TopicDTO> createTopic(Long userId, InputTopicDTO data) {
        UserDTO actingUser = userServiceClient.getUserById(userId);
        CourseDTO course = courseServiceClient.getCourseById(data.courseId());
        log.info("Creating topic by user ID: {}", actingUser.id());

        existsByTitle(data.title());
        existsByDescription(data.description());

        Mono<ContentValidationResponse> titleValidation = contentValidationClient.validateContent(data.title());
        Mono<ContentValidationResponse> descriptionValidation = contentValidationClient.validateContent(data.description());

        return Mono.zip(titleValidation, descriptionValidation)
                .publishOn(Schedulers.boundedElastic())
                .map(validationResults -> {
                    ContentValidationResponse titleResult = validationResults.getT1();
                    ContentValidationResponse descResult = validationResults.getT2();

                    // Validate the title and description content using AI
                    validateTitleContent(titleResult);
                    validateDescriptionContent(descResult);

                    TopicEntity topic = topicRepository.save(new TopicEntity(actingUser.id(), data.title(), data.description(), course.id()));

                    TopicDetailsDTO topicDetailsDTO = TopicDetailsDTO.fromEntity(topic, new ArrayList<>(), actingUser, course, new ArrayList<>());
                    TopicPayload topicPayload = new TopicPayload(topicDetailsDTO, actingUser.id());
                    topicEventPublisher.publishEvent(TopicEventType.CREATED, topicPayload);
                    return TopicDTO.fromEntity(topic, actingUser, course, 0L);
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

        List<TopicDTO> topicDTOs = mapTopicsToDTOs(topicsPage.getContent());
        return new PageImpl<>(topicDTOs, pageable, topicsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicDTO> getAllTopicsByUser(Long userId, Pageable pageable, String keyword) {
        Page<TopicEntity> topicsPage;
        UserDTO user = userServiceClient.getUserById(userId);
        log.debug("Fetching topics for user ID: {} - keyword: {}", user.id(), keyword);

        if (keyword != null ) {
            topicsPage = topicRepository.findByUserIdWithKeyword(user.id(), keyword, pageable);
        } else {
            topicsPage = topicRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(user.id(), pageable);
        }

        List<TopicDTO> topicDTOs = mapTopicsToDTOs(topicsPage.getContent());
        return new PageImpl<>(topicDTOs, pageable, topicsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public TopicDetailsDTO getTopicById(Long topicId) {
        log.info("Fetching topic details with ID: {}", topicId);
        TopicEntity topic = findTopicById(topicId);

        UserDTO user = userServiceClient.getUserById(topic.getUserId());
        CourseDTO course = courseServiceClient.getCourseById(topic.getCourseId());

        List<ReplyDTO> replies = replyServiceClient.getAllRepliesByTopic(topic.getId());
        List<TopicFollowerDTO> followers = topicFollowService.mapTopicFollowerToDTOs(topic.getFollowedTopics());

        return TopicDetailsDTO.fromEntity(topic, replies, user, course, followers);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicSummaryDTO getTopicSummaryById(Long topicId) {
        log.info("Fetching topic summary with ID: {}", topicId);
        TopicEntity topic = findTopicById(topicId);
        UserDTO user = userServiceClient.getUserById(topic.getUserId());

        return TopicSummaryDTO.fromEntity(topic, user);
    }

    @Override
    @Transactional
    public Mono<TopicDetailsDTO> updateTopic(Long userId, InputTopicDTO data, Long topicId) {
        TopicEntity topic = findTopicById(topicId);
        UserDTO actingUser = checkModificationPermission(userId, topic);
        CourseDTO course = courseServiceClient.getCourseById(data.courseId());
        log.info("Updating topic ID: {} by user ID: {}", topicId, actingUser.id());


        Mono<ContentValidationResponse> titleValidation = Mono.just(new ContentValidationResponse("approved"));
        Mono<ContentValidationResponse> descriptionValidation = Mono.just(new ContentValidationResponse("approved"));


        if (!data.title().equals(topic.getTitle())) {
            existsByTitle(data.title());
            titleValidation = contentValidationClient.validateContent(data.title());
        }

        if (!data.description().equals(topic.getDescription())) {
            existsByDescription(data.description());
            descriptionValidation = contentValidationClient.validateContent(data.description());
        }

        return Mono.zip(titleValidation, descriptionValidation)
                .publishOn(Schedulers.boundedElastic())
                .map(validationResults -> {

                    ContentValidationResponse titleResult = validationResults.getT1();
                    ContentValidationResponse descResult = validationResults.getT2();

                    // Validate the new title and description content using AI
                    validateTitleContent(titleResult);
                    validateDescriptionContent(descResult);

                    topic.setTitle(data.title());
                    topic.setDescription(data.description());
                    topic.setCourseId(course.id());

                    TopicEntity updatedTopic = topicRepository.save(topic);
                    log.info("Topic updated ID: {} by user ID: {}", updatedTopic.getId(), actingUser.id());

                    List<ReplyDTO> replies = replyServiceClient.getAllRepliesByTopic(updatedTopic.getId());
                    UserDTO author = userServiceClient.getUserById(topic.getUserId());
                    List<TopicFollowerDTO> followers = topicFollowService.mapTopicFollowerToDTOs(topic.getFollowedTopics());

                    TopicDetailsDTO topicDetailsDTO = TopicDetailsDTO.fromEntity(topic, replies, author, course, followers);
                    TopicPayload topicPayload = new TopicPayload(topicDetailsDTO, actingUser.id());
                    topicEventPublisher.publishEvent(TopicEventType.UPDATED, topicPayload);
                    return topicDetailsDTO;
                });
    }

    @Override
    @Transactional
    public void deleteTopic(Long userId, Long topicId) {
        TopicEntity topic = findTopicById(topicId);
        UserDTO actingUser = checkModificationPermission(userId, topic);
        CourseDTO course = courseServiceClient.getCourseById(topic.getCourseId());
        topic.setIsDeleted(true); //topicRepository.delete(topic);
        log.info("Topic marked as deleted - ID: {} by user ID: {}", topicId, actingUser.id());

        List<ReplyDTO> replies = replyServiceClient.getAllRepliesByTopic(topic.getId());
        UserDTO author = userServiceClient.getUserById(topic.getUserId());
        List<TopicFollowerDTO> followers = topicFollowService.mapTopicFollowerToDTOs(topic.getFollowedTopics());

        TopicDetailsDTO topicDetailsDTO = TopicDetailsDTO.fromEntity(topic, replies, author, course, followers);
        TopicPayload topicPayload = new TopicPayload(topicDetailsDTO, actingUser.id());
        topicEventPublisher.publishEvent(TopicEventType.DELETED, topicPayload);
    }

    @Override
    @Transactional
    public TopicDetailsDTO changeTopicStatus(InputTopicStatusDTO data, Long topicId) {
        UserDTO actingUser = userServiceClient.getUserById(data.userId());
        TopicEntity topic = findTopicById(topicId);
        CourseDTO course = courseServiceClient.getCourseById(topic.getCourseId());
        checkModificationPermission(data.userId(), topic);
        topic.setStatus(data.status());
        log.info("Topic status changed with ID: {}, by user ID: {}", topic.getId(), actingUser.id());

        List<ReplyDTO> replies = replyServiceClient.getAllRepliesByTopic(topic.getId());
        UserDTO author = userServiceClient.getUserById(topic.getUserId());
        List<TopicFollowerDTO> followers = topicFollowService.mapTopicFollowerToDTOs(topic.getFollowedTopics());

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
                UserDTO user = userServiceClient.getUserById(topic.getUserId());
                CourseDTO course = courseServiceClient.getCourseById(topic.getCourseId());
                List<ReplyDTO> replies = replyServiceClient.getAllRepliesByTopic(topic.getId());
                List<TopicFollowerDTO> followers = topicFollowService.mapTopicFollowerToDTOs(topic.getFollowedTopics());

                TopicDetailsDTO dto = TopicDetailsDTO.fromEntity(topic, replies, user, course, followers);
                result.add(dto);
            } catch (Exception e) {
                log.error("Error exporting topic {}: {}", topic.getId(), e.getMessage());
            }
        }

        log.info("Export completed: {} topics exported.", result.size());
        return result;
    }


    private UserDTO checkModificationPermission(Long userId, TopicEntity topic) {
        UserDTO user = userServiceClient.getUserById(userId);
        // Si el usuario es el propietario O tiene permisos elevados, puede modificar el topic
        if (!topic.getUserId().equals(user.id()) && !user.hasElevatedPermissions()) {
            log.warn("User without permission ID: {} attempted to modify topic ID: {}", user.id(), topic.getId());
            throw new AppException("No tienes permiso para realizar cambios en este tópico", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    private void existsByTitle(String title) {
        if (topicRepository.existsByTitleAndIsDeletedFalse(title)) {
            log.warn("A topic already exists with title: {}", title);
            throw new AppException("El titulo ya existe.", HttpStatus.CONFLICT);
        }
    }

    private void existsByDescription(String description) {
        if (topicRepository.existsByDescriptionAndIsDeletedFalse(description)) {
            log.warn("A topic already exists with description: {}", description);
            throw new AppException("La descripción ya existe.", HttpStatus.CONFLICT);
        }
    }

    public TopicEntity findTopicById(Long topicId) {
        return topicRepository.findByIdAndIsDeletedFalse(topicId)
                .orElseThrow(() -> {
                    log.warn("Topic not found with ID: {}", topicId);
                    return new AppException("Tópico no encontrado", HttpStatus.NOT_FOUND);
                });
    }

    private void validateTitleContent(ContentValidationResponse validationResponse) {
        if (!"approved".equals(validationResponse.result())) {
            log.warn("Title content not approved: {}", validationResponse.result());
            throw new AppException("El título " + validationResponse.result(), HttpStatus.FORBIDDEN);
        }
    }


    private void validateDescriptionContent(ContentValidationResponse validationResponse) {
        if (!"approved".equals(validationResponse.result())) {
            log.warn("Description content not approved: {}", validationResponse.result());
            throw new AppException("La descripción " + validationResponse.result(), HttpStatus.FORBIDDEN);
        }
    }

    public List<TopicDTO> mapTopicsToDTOs(List<TopicEntity> topics) {


        List<Long> userIds = topics.stream()
                .map(TopicEntity::getUserId)
                .distinct()
                .toList();

        List<Long> courseIds = topics.stream()
                .map(TopicEntity::getCourseId)
                .distinct()
                .toList();

        List<UserDTO> users = userServiceClient.getUsersByIds(userIds);
        List<CourseDTO> courses = courseServiceClient.getCoursesByIds(courseIds);
        List<ReplyCountDTO> repliesCount = replyServiceClient.getReplyCountsByTopicIds(topics.stream().map(TopicEntity::getId).toList());

        Map<Long, UserDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserDTO::id, u -> u));

        Map<Long, CourseDTO> courseMap = courses.stream()
                .collect(Collectors.toMap(CourseDTO::id, c -> c));


        return topics.stream()
                .map(topic -> {
                    UserDTO user = userMap.get(topic.getUserId());
                    CourseDTO course = courseMap.get(topic.getCourseId());
                    Long count = repliesCount.stream()
                            .filter(r -> r.topicId().equals(topic.getId()))
                            .findFirst()
                            .map(ReplyCountDTO::count)
                            .orElse(0L);
                    return TopicDTO.fromEntity(topic, user, course, count);
                })
                .toList();
    }

}
