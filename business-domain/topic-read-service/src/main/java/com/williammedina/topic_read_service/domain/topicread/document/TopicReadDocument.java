package com.williammedina.topic_read_service.domain.topicread.document;

import com.williammedina.topic_read_service.domain.topicread.model.Author;
import com.williammedina.topic_read_service.domain.topicread.model.Course;
import com.williammedina.topic_read_service.domain.topicread.model.Follower;
import com.williammedina.topic_read_service.domain.topicread.model.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "topics_read")
public class TopicReadDocument {
    @Id
    private Long id;

    private String title;
    private String description;

    private Course course;
    private Author author;

    private List<Reply> replies;
    private List<Follower> followers;

    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        ACTIVE,
        CLOSED,
    }

}
