package com.williammedina.email_service.infrastructure.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.email_service.domain.email.service.EmailService;
import com.williammedina.email_service.domain.email.dto.ReplyDTO;
import com.williammedina.email_service.domain.email.dto.TopicSummaryDTO;
import com.williammedina.email_service.infrastructure.event.model.reply.ReplyEvent;
import com.williammedina.email_service.infrastructure.event.model.reply.ReplyPayload;
import com.williammedina.email_service.infrastructure.persistence.processedevent.ProcessedEventEntity;
import com.williammedina.email_service.infrastructure.persistence.processedevent.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReplyEventConsumer {

    private final EmailService emailService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public Consumer<ReplyEvent> replyEvents() {
        return event -> {
            log.info("Received event from reply-service: {}", event);

            try {

                if (processedEventRepository.existsById(event.eventId())) {
                    return;
                }

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

                processedEventRepository.save(new ProcessedEventEntity(
                        event.eventId(),
                        event.eventType().name(),
                        event.sourceService()
                ));

            } catch (Exception e) {
                log.error("Error processing event from reply-service: {}", e.getMessage(), e);
            }
        };
    }
}

