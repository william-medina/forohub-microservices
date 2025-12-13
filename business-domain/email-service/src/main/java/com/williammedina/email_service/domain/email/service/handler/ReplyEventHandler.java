package com.williammedina.email_service.domain.email.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.email_service.domain.email.dto.ReplyDTO;
import com.williammedina.email_service.domain.email.dto.TopicSummaryDTO;
import com.williammedina.email_service.domain.email.service.EmailService;
import com.williammedina.email_service.infrastructure.event.model.reply.ReplyEvent;
import com.williammedina.email_service.infrastructure.event.model.reply.ReplyPayload;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyEventHandler implements DomainEventHandler<ReplyEvent> {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(ReplyEvent event) throws MessagingException {

        ReplyPayload payload = objectMapper.convertValue(event.payload(), ReplyPayload.class);
        ReplyDTO reply = payload.reply();
        TopicSummaryDTO topic = payload.topic();
        String courseName = payload.courseName();
        Long userId = payload.userId();

        boolean isNotTopicAuthor = !userId.equals(topic.author().id());

        switch (event.eventType()) {
            case CREATED -> {
                if(isNotTopicAuthor) {
                    log.info("Sending email to topic owner ID: {} about new reply", topic.id());
                    emailService.notifyTopicReply(topic, courseName, userId);
                }
                log.info("Sending email to followers of topic ID: {}", topic.id());
                emailService.notifyFollowersTopicReply(topic, courseName, userId);
            }
            case UPDATED -> {
                if(isNotTopicAuthor) {
                    log.info("Sending email to reply owner ID: {} about reply update", reply.id());
                    emailService.notifyReplyEdited(reply, topic, courseName);
                }
            }
            case SOLUTION_CHANGED -> {
                if(reply.solution()) {
                    log.info("Sending email for reply marked as solution ID: {}", reply.id());
                    emailService.notifyReplySolved(reply, topic, courseName);
                }
            }
            case DELETED -> {
                if(isNotTopicAuthor) {
                    log.info("Sending email for deletion to reply owner ID: {}", reply.id());
                    emailService.notifyReplyDeleted(reply, topic, courseName);
                }
            }
        }
    }
}
