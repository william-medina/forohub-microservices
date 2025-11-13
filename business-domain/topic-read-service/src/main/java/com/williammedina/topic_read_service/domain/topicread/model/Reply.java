package com.williammedina.topic_read_service.domain.topicread.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reply {
    private Long id;
    private Long topicId;
    private String content;
    private Author author;
    private Boolean solution;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
