package com.williammedina.topic_read_service.domain.topicread.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williammedina.topic_read_service.domain.topicread.document.TopicReadDocument;
import com.williammedina.topic_read_service.domain.topicread.dto.UserDTO;
import com.williammedina.topic_read_service.domain.topicread.model.Follower;
import com.williammedina.topic_read_service.domain.topicread.model.Reply;
import com.williammedina.topic_read_service.domain.topicread.repository.TopicReadRepository;
import com.williammedina.topic_read_service.infrastructure.event.model.user.UserEvent;
import com.williammedina.topic_read_service.infrastructure.event.model.user.UserEventType;
import com.williammedina.topic_read_service.infrastructure.event.model.user.UserPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventHandler implements DomainEventHandler<UserEvent> {

    private final TopicReadRepository topicReadRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(UserEvent event) {
        UserPayload payload = objectMapper.convertValue(event.payload(), UserPayload.class);
        UserDTO user = payload.user();

        if (event.eventType() == UserEventType.UPDATED_USER) {
            log.info("Updating username for user {} -> {}", user.id(), user.username());

            // Buscar todos los topics
            List<TopicReadDocument> allTopics = topicReadRepository.findAll();
            for (TopicReadDocument topic : allTopics) {
                boolean updated = false;

                // Actualizar autor del topic
                if (topic.getAuthor() != null && topic.getAuthor().getId().equals(user.id())) {
                    topic.getAuthor().setUsername(user.username());
                    topic.getAuthor().setProfile(user.profile());
                    updated = true;
                }

                // Actualizar autor en replies
                if (topic.getReplies() != null) {
                    for (Reply reply : topic.getReplies()) {
                        if (reply.getAuthor() != null && reply.getAuthor().getId().equals(user.id())) {
                            reply.getAuthor().setUsername(user.username());
                            reply.getAuthor().setProfile(user.profile());
                            updated = true;
                        }
                    }
                }

                // Actualizar usuario en followers
                if (topic.getFollowers() != null) {
                    for (Follower follower : topic.getFollowers()) {
                        if (follower.getUser() != null && follower.getUser().getId().equals(user.id())) {
                            follower.getUser().setUsername(user.username());
                            follower.getUser().setProfile(user.profile());
                            updated = true;
                        }
                    }
                }


                // Guardar solo si hubo cambios
                if (updated) {
                    topicReadRepository.save(topic);
                    log.info("Updated username in topic {}", topic.getId());
                }
            }
        }
    }
}
