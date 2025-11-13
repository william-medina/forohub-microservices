package com.williammedina.topic_read_service.infrastructure.persistence.processedevent;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "processed_events")
public class ProcessedEventDocument {

    @Id
    private String eventId;

    private String eventType;
    private String sourceService;
    private LocalDateTime processedAt;

    public ProcessedEventDocument(String eventId, String eventType, String sourceService) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.sourceService = sourceService;
        this.processedAt = LocalDateTime.now();
    }
}
