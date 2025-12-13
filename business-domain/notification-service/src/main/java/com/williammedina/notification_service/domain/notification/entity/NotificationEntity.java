package com.williammedina.notification_service.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity(name = "Notification")
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "topic_id")
    private Long topicId;

    @Column(name = "reply_id")
    private Long replyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subtype subtype;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public NotificationEntity(Long userId, Long topicId, Long replyId, String title, String message, Type type, Subtype subtype) {
        this.userId = userId;
        this.topicId = topicId;
        this.replyId = replyId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.subtype = subtype;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public enum Type {
        TOPIC,
        REPLY
    }

    public enum Subtype {
        REPLY,
        EDITED,
        SOLVED,
        DELETED
    }
}
