package com.williammedina.email_service.infrastructure.email;

import com.williammedina.email_service.domain.email.dto.ReplyDTO;
import com.williammedina.email_service.domain.email.dto.TopicDetailsDTO;
import com.williammedina.email_service.domain.email.dto.TopicSummaryDTO;
import com.williammedina.email_service.domain.email.dto.UserDTO;
import com.williammedina.email_service.domain.email.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(value = "email.enabled", havingValue = "false", matchIfMissing = true)
public class DisabledEmailService implements EmailService {

    @Override
    public void sendConfirmationEmail(UserDTO user, String token) throws MessagingException {
        log.info("[DISABLED EMAIL] Account confirmation email for user ID: {}", user.id());
    }

    @Override
    public void sendPasswordResetEmail(UserDTO user, String token) throws MessagingException {
        log.info("[DISABLED EMAIL] Password reset requested by user ID: {}", user.id());
    }

    @Override
    public void notifyTopicReply(TopicSummaryDTO topic, String courseName, Long userId) throws MessagingException {
        log.info("[DISABLED EMAIL] Skipping topic reply notification. Topic: {}, Course: {}, UserId: {}",
                topic.title(), courseName, userId);
    }

    @Override
    public void notifyTopicSolved(TopicDetailsDTO topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Skipping topic solved notification. Topic: {}", topic.title());
    }

    @Override
    public void notifyTopicEdited(TopicDetailsDTO topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Skipping topic edited notification. Topic: {}", topic.title());
    }

    @Override
    public void notifyTopicDeleted(TopicDetailsDTO topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Skipping topic deleted notification. Topic: {}", topic.title());
    }

    @Override
    public void notifyReplySolved(ReplyDTO reply, TopicSummaryDTO topic, String courseName) throws MessagingException {
        log.info("[DISABLED EMAIL] Skipping reply solved notification. ReplyId: {}, Topic: {}, Course: {}",
                reply.author().id(), topic.title(), courseName);
    }

    @Override
    public void notifyReplyEdited(ReplyDTO reply, TopicSummaryDTO topic, String courseName) throws MessagingException {
        log.info("[DISABLED EMAIL] Skipping reply edited notification. ReplyId: {}, Topic: {}, Course: {}",
                reply.author().id(), topic.title(), courseName);
    }

    @Override
    public void notifyReplyDeleted(ReplyDTO reply, TopicSummaryDTO topic, String courseName) throws MessagingException {
        log.info("[DISABLED EMAIL] Skipping reply deleted notification. ReplyId: {}, Topic: {}, Course: {}",
                reply.author().id(), topic.title(), courseName);
    }

    @Override
    public void notifyFollowersTopicReply(TopicSummaryDTO topic, String courseName, Long userId) throws MessagingException {
        log.info("[DISABLED EMAIL] Skipping followers topic reply notification. Topic: {}, Course: {}, UserId: {}",
                topic.title(), courseName, userId);
    }

    @Override
    public void notifyFollowersTopicSolved(TopicDetailsDTO topic) throws MessagingException {
        log.info("[DISABLED EMAIL] Skipping followers topic solved notification. Topic: {}", topic.title());
    }
}
