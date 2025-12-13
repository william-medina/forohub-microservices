package com.williammedina.notification_service.domain.notification.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.notification_service.domain.notification.dto.ReplyDTO;
import com.williammedina.notification_service.domain.notification.dto.TopicSummaryDTO;
import com.williammedina.notification_service.domain.notification.service.NotificationService;
import com.williammedina.notification_service.infrastructure.event.model.reply.ReplyEvent;
import com.williammedina.notification_service.infrastructure.event.model.reply.ReplyPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyEventHandler implements DomainEventHandler<ReplyEvent> {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(ReplyEvent event) {
        ReplyPayload payload = objectMapper.convertValue(event.payload(), ReplyPayload.class);
        ReplyDTO reply = payload.reply();
        TopicSummaryDTO topic = payload.topic();
        String courseName = payload.courseName();
        Long userId = payload.userId();

        boolean isNotTopicAuthor = !userId.equals(topic.author().id());

        switch (event.eventType()) {
            case CREATED -> {
                if(isNotTopicAuthor) {
                    log.info("Notifying topic owner ID: {} about new reply", topic.id());
                    notificationService.notifyTopicReply(reply, topic, courseName);
                }
                log.info("Notifying followers of topic ID: {}", topic.id());
                notificationService.notifyFollowersTopicReply(reply, topic, courseName, userId);
            }
            case UPDATED -> {
                if(isNotTopicAuthor) {
                    log.info("Notifying reply owner ID: {} about reply update", reply.id());
                    notificationService.notifyReplyEdited(reply, topic, courseName);
                }
            }
            case SOLUTION_CHANGED -> {
                if(reply.solution()) {
                    log.info("Sending notifications for reply marked as solution ID: {}", reply.id());
                    notificationService.notifyReplySolved(reply, topic, courseName);
                }
            }
            case DELETED -> {
                if(isNotTopicAuthor) {
                    log.info("Notifying reply owner ID: {} about deletion", reply.id());
                    notificationService.notifyReplyDeleted(reply, topic, courseName);
                }
            }
        }
    }
}
