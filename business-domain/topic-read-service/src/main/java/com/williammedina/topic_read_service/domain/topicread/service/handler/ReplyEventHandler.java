package com.williammedina.topic_read_service.domain.topicread.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.topic_read_service.domain.topicread.dto.*;
import com.williammedina.topic_read_service.domain.topicread.model.Reply;
import com.williammedina.topic_read_service.domain.topicread.repository.TopicReadRepository;
import com.williammedina.topic_read_service.infrastructure.event.model.reply.ReplyEvent;
import com.williammedina.topic_read_service.infrastructure.event.model.reply.ReplyPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyEventHandler implements DomainEventHandler<ReplyEvent> {

    private final TopicReadRepository topicReadRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(ReplyEvent event) {

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
    }
}
