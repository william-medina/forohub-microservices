package com.williammedina.reply_service.domain.reply.service;

import com.williammedina.reply_service.domain.reply.dto.*;
import com.williammedina.reply_service.domain.reply.entity.ReplyEntity;
import com.williammedina.reply_service.domain.reply.repository.ReplyRepository;
import com.williammedina.reply_service.domain.reply.service.finder.ReplyFinder;
import com.williammedina.reply_service.domain.reply.service.permission.ReplyPermissionService;
import com.williammedina.reply_service.domain.reply.service.query.ReplyQueryMapper;
import com.williammedina.reply_service.domain.reply.service.validator.ReplyValidator;
import com.williammedina.reply_service.infrastructure.client.ContentValidationClient;
import com.williammedina.reply_service.infrastructure.client.CourseServiceClient;
import com.williammedina.reply_service.infrastructure.client.TopicServiceClient;
import com.williammedina.reply_service.infrastructure.client.UserServiceClient;
import com.williammedina.reply_service.infrastructure.event.model.ReplyEventType;
import com.williammedina.reply_service.infrastructure.event.model.ReplyPayload;
import com.williammedina.reply_service.infrastructure.event.publisher.ReplyEventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final UserServiceClient userServiceClient;
    private final TopicServiceClient topicServiceClient;
    private final ContentValidationClient contentValidationClient;
    private final ReplyEventPublisher replyEventPublisher;
    private final CourseServiceClient courseServiceClient;
    private final ReplyValidator validator;
    private final ReplyFinder replyFinder;
    private final ReplyPermissionService replyPermissionService;
    private final ReplyQueryMapper replyQueryMapper;

    @Override
    @Transactional
    public Mono<ReplyDTO> createReply(Long userId, CreateReplyDTO data) {
        UserDTO currentUser = userServiceClient.getUserById(userId);
        TopicSummaryDTO topic = topicServiceClient.getTopicSummaryById(data.topicId());
        CourseDTO course = courseServiceClient.getCourseById(topic.courseId());
        log.info("User ID: {} creating reply for topic ID: {}", currentUser.id(), topic.id());

        validator.ensureTopicIsOpen(topic);

        return contentValidationClient.validateContent(data.content())
                .publishOn(Schedulers.boundedElastic())
                .map(contentResult -> {
                    validator.ensureReplyContentIsValid(contentResult); // Validate the reply content using AI

                    ReplyEntity newReply = replyRepository.save(new ReplyEntity(currentUser.id(), topic.id(), data.content()));
                    log.info("Reply created with ID: {} by user ID: {}", newReply.getId(), currentUser.id());

                    ReplyDTO replyDTO = ReplyDTO.fromEntity(newReply, currentUser);
                    ReplyPayload replyPayload = new ReplyPayload(replyDTO, topic, course.name(), currentUser.id());
                    replyEventPublisher.publishEvent(ReplyEventType.CREATED, replyPayload);
                    return replyDTO;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReplyDTO> getAllRepliesByUser(Long userId, Pageable pageable) {
        UserDTO currentUser = userServiceClient.getUserById(userId);
        log.debug("Fetching replies for user ID: {}", currentUser.id());
        Page<ReplyEntity> replies = replyRepository.findByUserIdSorted(currentUser.id(), pageable);
        List<ReplyDTO> replyDTOS = replyQueryMapper.mapReplyToDTOs(replies.getContent());
        return new PageImpl<>(replyDTOS, pageable, replies.getTotalElements());
    }

    @Override
    @Transactional
    public Mono<ReplyDTO> updateReply(Long userId, UpdateReplyDTO data, Long replyId) {
        ReplyEntity replyToUpdate = replyFinder.findReplyById(replyId);
        UserDTO currentUser = replyPermissionService.checkCanModify(userId, replyToUpdate);
        TopicSummaryDTO topic = topicServiceClient.getTopicSummaryById(replyToUpdate.getTopicId());
        CourseDTO course = courseServiceClient.getCourseById(topic.courseId());

        log.info("User ID: {} updating reply ID: {}", currentUser.id(), replyId);

        return contentValidationClient.validateContent(data.content())
            .publishOn(Schedulers.boundedElastic())
            .map(contentResult -> {

                validator.ensureReplyContentIsValid(contentResult); // Validate the updated reply content using AI

                replyToUpdate.setContent(data.content());
                ReplyEntity updatedReply = replyRepository.save(replyToUpdate);

                UserDTO author  = userServiceClient.getUserById(updatedReply.getUserId());

                ReplyDTO replyDTO = ReplyDTO.fromEntity(updatedReply, author);
                ReplyPayload replyPayload = new ReplyPayload(replyDTO, topic, course.name(), currentUser.id());
                replyEventPublisher.publishEvent(ReplyEventType.UPDATED, replyPayload);
                return replyDTO;
            });
    }

    @Override
    @Transactional
    public void deleteReply(Long userId, Long replyId) {
        ReplyEntity replyToDelete = replyFinder.findReplyById(replyId);
        UserDTO currentUser = replyPermissionService.checkCanModify(userId, replyToDelete);
        TopicSummaryDTO topic = topicServiceClient.getTopicSummaryById(replyToDelete.getTopicId());
        CourseDTO course = courseServiceClient.getCourseById(topic.courseId());

        replyPermissionService.checkCannotDeleteSolution(replyToDelete);

        replyToDelete.markAsDeleted(); //retryRepository.delete(response);
        log.info("Reply ID: {} marked as deleted by user ID: {}", replyId, currentUser.id());

        UserDTO author  = userServiceClient.getUserById(replyToDelete.getUserId());
        ReplyDTO replyDTO = ReplyDTO.fromEntity(replyToDelete, author);
        ReplyPayload replyPayload = new ReplyPayload(replyDTO, topic, course.name(), currentUser.id());
        replyEventPublisher.publishEvent(ReplyEventType.DELETED, replyPayload);
    }

    @Override
    @Transactional(readOnly = true)
    public ReplyDTO getReplyById(Long replyId) {
        log.debug("Fetching reply with ID: {}", replyId);
        ReplyEntity reply = replyFinder.findReplyById(replyId);
        UserDTO user = userServiceClient.getUserById(reply.getUserId());
        return ReplyDTO.fromEntity(reply, user);
    }

    @Override
    @Transactional
    public ReplyDTO setCorrectReply(Long userId, Long replyId) {
        ReplyEntity reply = replyFinder.findReplyById(replyId);
        UserDTO currentUser = userServiceClient.getUserById(userId);
        TopicSummaryDTO topic = topicServiceClient.getTopicSummaryById(reply.getTopicId());
        CourseDTO course = courseServiceClient.getCourseById(topic.courseId());

        replyPermissionService.checkElevatedPermissionsForSolution(currentUser, replyId);

        log.info("User ID: {} changing status of reply ID: {}", currentUser.id(), replyId);
        List<ReplyEntity> replies = replyRepository.findByTopicIdAndIsDeletedFalseOrderByCreatedAtDesc(reply.getTopicId());

        boolean isCurrentlySolution = reply.getSolution();
        replies.forEach(re -> re.setSolution(false)); // Deactivate all solutions

        if (!isCurrentlySolution) {
            reply.setSolution(true);
            topicServiceClient.changeTopicStatus(reply.getTopicId(), new InputTopicStatusDTO(TopicSummaryDTO.Status.CLOSED, currentUser.id()));
            log.info("Reply ID: {} marked as solution for topic ID: {}", reply.getId(), reply.getTopicId());
        } else {
            topicServiceClient.changeTopicStatus(reply.getTopicId(), new InputTopicStatusDTO(TopicSummaryDTO.Status.ACTIVE, currentUser.id()));
            log.info("Reply ID: {} unmarked as solution for topic ID: {}", reply.getId(), reply.getTopicId());
        }

        replyRepository.saveAll(replies);

        UserDTO author  = userServiceClient.getUserById(reply.getUserId());

        ReplyDTO replyDTO = ReplyDTO.fromEntity(reply, author);
        ReplyPayload replyPayload = new ReplyPayload(replyDTO, topic, course.name(), currentUser.id());
        replyEventPublisher.publishEvent(ReplyEventType.SOLUTION_CHANGED, replyPayload);
        return replyDTO;
    }

}
