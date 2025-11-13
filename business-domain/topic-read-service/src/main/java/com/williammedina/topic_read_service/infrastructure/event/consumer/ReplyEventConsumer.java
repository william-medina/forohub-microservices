package com.williammedina.topic_read_service.infrastructure.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.topic_read_service.domain.topicread.dto.ReplyDTO;
import com.williammedina.topic_read_service.domain.topicread.dto.TopicSummaryDTO;
import com.williammedina.topic_read_service.domain.topicread.model.Reply;
import com.williammedina.topic_read_service.domain.topicread.repository.TopicReadRepository;
import com.williammedina.topic_read_service.infrastructure.event.model.reply.ReplyEvent;
import com.williammedina.topic_read_service.infrastructure.event.model.reply.ReplyPayload;
import com.williammedina.topic_read_service.infrastructure.persistence.processedevent.ProcessedEventDocument;
import com.williammedina.topic_read_service.infrastructure.persistence.processedevent.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReplyEventConsumer {

    private final ProcessedEventRepository processedEventRepository;
    private final TopicReadRepository topicReadRepository;
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


                topicReadRepository.findById(topic.id()).ifPresent(topicDoc -> {
                    List<Reply> replies = topicDoc.getReplies();

                    switch (event.eventType()) {
                        case CREATED -> {
                            replies.add(ReplyDTO.fromDto(reply));
                            topicDoc.setReplies(replies);
                            topicReadRepository.save(topicDoc);
                            log.info("Added reply {} to topic {}", reply.id(), topic.id());
                        }
                        case UPDATED -> {
                            replies.stream()
                                    .filter(r -> r.getId().equals(reply.id()))
                                    .findFirst()
                                    .ifPresent(existing -> {
                                        int idx = replies.indexOf(existing);
                                        replies.set(idx, ReplyDTO.fromDto(reply));
                                        topicDoc.setReplies(replies);
                                        topicReadRepository.save(topicDoc);
                                        log.info("Updated reply {} in topic {}", reply.id(), topic.id());
                                    });
                        }
                        case SOLUTION_CHANGED -> {
                            replies.forEach(r -> {
                                r.setSolution(r.getId().equals(reply.id()));
                            });
                            topicDoc.setReplies(replies);
                            topicReadRepository.save(topicDoc);
                            log.info("Marked reply {} as solution in topic {}", reply.id(), topic.id());
                        }
                        case DELETED -> {
                            replies.removeIf(r -> r.getId().equals(reply.id()));
                            topicDoc.setReplies(replies);
                            topicReadRepository.save(topicDoc);
                            log.info("Deleted reply {} from topic {}", reply.id(), topic.id());
                        }
                    }
                });

                processedEventRepository.save(new ProcessedEventDocument(
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

