package com.williammedina.notification_service.infrastructure.persistence.processedevent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity(name = "ProcessedEvent")
@Table(name = "processed_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "eventId")
public class ProcessedEventEntity {

    @Id
    @Column(name = "event_id", nullable = false, length = 36)
    private String eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "source_service", nullable = false, length = 100)
    private String sourceService;

    @Column(name = "processed_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime processedAt;

    public ProcessedEventEntity(String eventId, String eventType, String sourceService) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.sourceService = sourceService;
    }
}
