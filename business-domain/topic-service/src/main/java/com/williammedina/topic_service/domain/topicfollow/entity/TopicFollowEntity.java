package com.williammedina.topic_service.domain.topicfollow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity(name = "TopicFollow")
@Table(name = "topic_followers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class TopicFollowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonIgnore
    private TopicEntity topic;

    @Column(name = "followed_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime followedAt;

    public TopicFollowEntity(Long userId, TopicEntity topic) {
        this.userId = userId;
        this.topic = topic;
    }
}
