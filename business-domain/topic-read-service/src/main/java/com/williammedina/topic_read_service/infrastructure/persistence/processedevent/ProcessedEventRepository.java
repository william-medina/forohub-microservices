package com.williammedina.topic_read_service.infrastructure.persistence.processedevent;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessedEventRepository extends MongoRepository<ProcessedEventDocument, String> {
}
