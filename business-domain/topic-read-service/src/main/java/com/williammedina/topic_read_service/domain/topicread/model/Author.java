package com.williammedina.topic_read_service.domain.topicread.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    private Long id;
    private String username;
    private String profile;
}
