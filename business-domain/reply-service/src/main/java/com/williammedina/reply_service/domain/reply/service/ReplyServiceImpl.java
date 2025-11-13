package com.williammedina.reply_service.domain.reply.service;

import com.williammedina.reply_service.domain.reply.dto.*;
import com.williammedina.reply_service.domain.reply.entity.ReplyEntity;
import com.williammedina.reply_service.domain.reply.repository.ReplyRepository;
import com.williammedina.reply_service.infrastructure.client.ContentValidationClient;
import com.williammedina.reply_service.infrastructure.client.CourseServiceClient;
import com.williammedina.reply_service.infrastructure.client.TopicServiceClient;
import com.williammedina.reply_service.infrastructure.client.UserServiceClient;
import com.williammedina.reply_service.infrastructure.event.model.ReplyEventType;
import com.williammedina.reply_service.infrastructure.event.model.ReplyPayload;
import com.williammedina.reply_service.infrastructure.event.publisher.ReplyEventPublisher;
import com.williammedina.reply_service.infrastructure.exception.AppException;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReplyServiceImpl implements ReplyService, InternalReplyService {

    private final ReplyRepository replyRepository;
    private final UserServiceClient userServiceClient;
    private final TopicServiceClient topicServiceClient;
    private final ContentValidationClient contentValidationClient;
    private final ReplyEventPublisher replyEventPublisher;
    private final CourseServiceClient courseServiceClient;

    @Override
    @Transactional
    public Mono<ReplyDTO> createReply(Long userId, CreateReplyDTO data) {
        UserDTO user = userServiceClient.getUserById(userId);
        TopicSummaryDTO topic = topicServiceClient.getTopicSummaryById(data.topicId());
        CourseDTO course = courseServiceClient.getCourseById(topic.courseId());
        log.info("User ID: {} creating reply for topic ID: {}", user.id(), topic.id());

        isTopicClosed(topic);

        return contentValidationClient.validateContent(data.content())
                .publishOn(Schedulers.boundedElastic())
                .map(contentResult -> {
                    validateReplyContent(contentResult); // Validate the reply content using AI

                    ReplyEntity reply = replyRepository.save(new ReplyEntity(user.id(), topic.id(), data.content()));
                    log.info("Reply created with ID: {} by user ID: {}", reply.getId(), user.id());

                    ReplyDTO replyDTO = ReplyDTO.fromEntity(reply, user);
                    ReplyPayload replyPayload = new ReplyPayload(replyDTO, topic, course.name(), user.id());
                    replyEventPublisher.publishEvent(ReplyEventType.CREATED, replyPayload);
                    return replyDTO;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReplyDTO> getAllRepliesByUser(Long userId, Pageable pageable) {
        UserDTO user = userServiceClient.getUserById(userId);
        log.debug("Fetching replies for user ID: {}", user.id());
        Page<ReplyEntity> replies = replyRepository.findByUserIdSorted(user.id(), pageable);
        List<ReplyDTO> replyDTOS = mapReplyToDTOs(replies.getContent());
        return new PageImpl<>(replyDTOS, pageable, replies.getTotalElements());
    }

    @Override
    @Transactional
    public Mono<ReplyDTO> updateReply(Long userId, UpdateReplyDTO data, Long replyId) {
        ReplyEntity reply = findReplyById(replyId);
        UserDTO actingUser = checkModificationPermission(userId, reply);
        TopicSummaryDTO topic = topicServiceClient.getTopicSummaryById(reply.getTopicId());
        CourseDTO course = courseServiceClient.getCourseById(topic.courseId());

        log.info("User ID: {} updating reply ID: {}", actingUser.id(), replyId);

        return contentValidationClient.validateContent(data.content())
            .publishOn(Schedulers.boundedElastic())
            .map(contentResult -> {

                validateReplyContent(contentResult); // Validate the updated reply content using AI

                reply.setContent(data.content());
                ReplyEntity updatedReply = replyRepository.save(reply);

                UserDTO author  = userServiceClient.getUserById(updatedReply.getUserId());

                ReplyDTO replyDTO = ReplyDTO.fromEntity(updatedReply, author);
                ReplyPayload replyPayload = new ReplyPayload(replyDTO, topic, course.name(), actingUser.id());
                replyEventPublisher.publishEvent(ReplyEventType.UPDATED, replyPayload);
                return replyDTO;
            });
    }

    @Override
    @Transactional
    public void deleteReply(Long userId, Long replyId) {
        ReplyEntity reply = findReplyById(replyId);
        UserDTO actingUser = checkModificationPermission(userId, reply);
        TopicSummaryDTO topic = topicServiceClient.getTopicSummaryById(reply.getTopicId());
        CourseDTO course = courseServiceClient.getCourseById(topic.courseId());

        if (reply.getSolution()) {
            throw new AppException("No puedes eliminar una respuesta marcada como solución", HttpStatus.CONFLICT);
        }

        reply.setIsDeleted(true); //retryRepository.delete(response);
        log.info("Reply ID: {} marked as deleted by user ID: {}", replyId, actingUser.id());

        UserDTO author  = userServiceClient.getUserById(reply.getUserId());
        ReplyDTO replyDTO = ReplyDTO.fromEntity(reply, author);
        ReplyPayload replyPayload = new ReplyPayload(replyDTO, topic, course.name(), actingUser.id());
        replyEventPublisher.publishEvent(ReplyEventType.DELETED, replyPayload);
    }

    @Override
    @Transactional(readOnly = true)
    public ReplyDTO getReplyById(Long replyId) {
        log.debug("Fetching reply with ID: {}", replyId);
        ReplyEntity reply = findReplyById(replyId);
        UserDTO user = userServiceClient.getUserById(reply.getUserId());
        return ReplyDTO.fromEntity(reply, user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReplyDTO> getAllRepliesByTopic(Long topicId) {
        log.debug("Fetching replies for topic ID: {}", topicId);
        TopicSummaryDTO topic = topicServiceClient.getTopicSummaryById(topicId);
        List<ReplyEntity> replies = replyRepository.findByTopicIdAndIsDeletedFalseOrderByCreatedAtDesc(topic.id());

        return mapReplyToDTOs(replies);
    }

    @Override
    @Transactional
    public ReplyDTO setCorrectReply(Long userId, Long replyId) {
        ReplyEntity reply = findReplyById(replyId);
        UserDTO actingUser = userServiceClient.getUserById(userId);
        TopicSummaryDTO topic = topicServiceClient.getTopicSummaryById(reply.getTopicId());
        CourseDTO course = courseServiceClient.getCourseById(topic.courseId());

        if (!actingUser .hasElevatedPermissions()) {
            log.warn("User ID: {} without permissions attempted to change reply ID: {}", actingUser.id(), replyId);
            throw new AppException("No tienes permiso para modificar el estado de la respuesta", HttpStatus.FORBIDDEN);
        }

        log.info("User ID: {} changing status of reply ID: {}", actingUser.id(), replyId);
        List<ReplyEntity> replies = replyRepository.findByTopicIdAndIsDeletedFalseOrderByCreatedAtDesc(reply.getTopicId());

        boolean isCurrentlySolution = reply.getSolution();
        replies.forEach(re -> re.setSolution(false)); // Deactivate all solutions

        System.out.println(reply.getTopicId() + " ----- " + new InputTopicStatusDTO(TopicSummaryDTO.Status.CLOSED, actingUser.id()));
        if (!isCurrentlySolution) {
            reply.setSolution(true);
            topicServiceClient.changeTopicStatus(reply.getTopicId(), new InputTopicStatusDTO(TopicSummaryDTO.Status.CLOSED, actingUser.id()));
            log.info("Reply ID: {} marked as solution for topic ID: {}", reply.getId(), reply.getTopicId());
        } else {
            topicServiceClient.changeTopicStatus(reply.getTopicId(), new InputTopicStatusDTO(TopicSummaryDTO.Status.ACTIVE, actingUser.id()));
            log.info("Reply ID: {} unmarked as solution for topic ID: {}", reply.getId(), reply.getTopicId());
        }

        replyRepository.saveAll(replies);

        UserDTO author  = userServiceClient.getUserById(reply.getUserId());

        ReplyDTO replyDTO = ReplyDTO.fromEntity(reply, author);
        ReplyPayload replyPayload = new ReplyPayload(replyDTO, topic, course.name(), actingUser.id());
        replyEventPublisher.publishEvent(ReplyEventType.SOLUTION_CHANGED, replyPayload);
        return replyDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public ReplyCountDTO getReplyCountByTopic(Long topicId) {
        log.debug("Fetching reply count for topic ID: {}", topicId);
        Long count = replyRepository.countByTopicIdAndIsDeletedFalse(topicId);
        return new ReplyCountDTO(topicId, count);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReplyCountDTO> getReplyCountsByTopicIds(List<Long> topicIds) {
        log.debug("Fetching reply counts for multiple topics: {}", topicIds);
        return replyRepository.countRepliesByTopicIds(topicIds);
    }

    @Override
    @Transactional(readOnly = true)
    public UserReplyCountDTO getReplyCountByUser(Long userId) {
        long count = replyRepository.countByUserId(userId);
        return new UserReplyCountDTO(count);
    }


    private ReplyEntity findReplyById(Long replyId) {
        return replyRepository.findByIdAndIsDeletedFalse(replyId)
                .orElseThrow(() ->  {
                    log.error("Reply not found with ID: {}", replyId);
                    return new AppException("Respuesta no encontrada", HttpStatus.NOT_FOUND);
                });
    }

    private UserDTO checkModificationPermission(Long userId, ReplyEntity reply) {
        UserDTO user = userServiceClient.getUserById(userId);
        // If the user is the owner OR has elevated permissions, they are allowed to modify the reply
        if (!reply.getUserId().equals(user.id()) && !user.hasElevatedPermissions()) {
            log.warn("User ID: {} without permission attempted to modify reply ID: {}", user.id(), reply.getId());
            throw new AppException("No tienes permiso para realizar cambios en esta respuesta", HttpStatus.FORBIDDEN);
        }

        return user;
    }


    private void isTopicClosed(TopicSummaryDTO topic) {
        if(topic.status().equals(TopicSummaryDTO.Status.CLOSED)) {
            log.warn("Attempted reply to closed topic - ID: {}", topic.id());
            throw new AppException("No se puede crear una respuesta. El tópico está cerrado.", HttpStatus.FORBIDDEN);
        }
    }

    private void validateReplyContent(ContentValidationResponse validationResponse) {
        if (!"approved".equals(validationResponse.result())) {
            log.warn("Reply content not approved: {}", validationResponse.result());
            throw new AppException("La respuesta " + validationResponse.result(), HttpStatus.FORBIDDEN);
        }
    }

    public List<ReplyDTO> mapReplyToDTOs(List<ReplyEntity> replies) {

        List<Long> userIds = replies.stream()
                .map(ReplyEntity::getUserId)
                .distinct()
                .toList();

        List<UserDTO> users = userServiceClient.getUsersByIds(userIds);

        Map<Long, UserDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserDTO::id, u -> u));

        return replies.stream()
                .map(response -> {
                    UserDTO user = userMap.get(response.getUserId());
                    return ReplyDTO.fromEntity(response, user);
                })
                .toList();
    }

}
