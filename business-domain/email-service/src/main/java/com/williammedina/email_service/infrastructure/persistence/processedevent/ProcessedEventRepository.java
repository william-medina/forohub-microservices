package com.williammedina.email_service.infrastructure.persistence.processedevent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, String> {

    boolean existsByEventId(String eventId);

}
