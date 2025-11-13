package com.williammedina.topic_read_service.infrastructure.init;

import com.williammedina.topic_read_service.domain.topicread.document.TopicReadDocument;
import com.williammedina.topic_read_service.domain.topicread.dto.*;
import com.williammedina.topic_read_service.domain.topicread.repository.TopicReadRepository;
import com.williammedina.topic_read_service.infrastructure.client.TopicServiceClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitialSyncService {

    private final TopicServiceClient topicServiceClient;
    private final TopicReadRepository topicReadRepository;

    @PostConstruct
    public void initializeData() {
        try {
            long count = topicReadRepository.count();
            if (count > 0) {
                log.info("MongoDB already has {} topics — skipping initial synchronization", count);
                return;
            }

            log.info("No topics found in MongoDB — starting initial synchronization from topic-service...");

            List<TopicDetailsDTO> topics = topicServiceClient.exportAllTopics();

            if (topics == null || topics.isEmpty()) {
                log.warn("No topics returned from topic-service export endpoint.");
                return;
            }

            List<TopicReadDocument> documents = topics.stream().map(TopicDetailsDTO::fromDto).toList();

            topicReadRepository.saveAll(documents);
            log.info("Successfully synchronized {} topics into MongoDB.", documents.size());

        } catch (Exception e) {
            log.error("Error during initial data synchronization: {}", e.getMessage(), e);
        }
    }
}
