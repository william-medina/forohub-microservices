package com.williammedina.topic_read_service.domain.topicread.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follower {
    private Author user;
    private LocalDateTime followedAt;
}