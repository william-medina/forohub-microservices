package com.williammedina.topic_service.domain.topic.service.query;

import com.williammedina.topic_service.domain.topic.dto.CourseDTO;
import com.williammedina.topic_service.domain.topic.dto.ReplyCountDTO;
import com.williammedina.topic_service.domain.topic.dto.TopicDTO;
import com.williammedina.topic_service.domain.topic.dto.UserDTO;
import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import com.williammedina.topic_service.infrastructure.client.CourseServiceClient;
import com.williammedina.topic_service.infrastructure.client.ReplyServiceClient;
import com.williammedina.topic_service.infrastructure.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicQueryMapper {

    private final CourseServiceClient courseServiceClient;
    private final ReplyServiceClient replyServiceClient;
    private final UserServiceClient userServiceClient;

    public List<TopicDTO> mapTopicsToDTOs(List<TopicEntity> topics) {

        List<Long> userIds = topics.stream()
                .map(TopicEntity::getUserId)
                .distinct()
                .toList();

        List<Long> courseIds = topics.stream()
                .map(TopicEntity::getCourseId)
                .distinct()
                .toList();

        List<UserDTO> users = userServiceClient.getUsersByIds(userIds);
        List<CourseDTO> courses = courseServiceClient.getCoursesByIds(courseIds);
        List<ReplyCountDTO> repliesCount = replyServiceClient.getReplyCountsByTopicIds(topics.stream().map(TopicEntity::getId).toList());

        Map<Long, UserDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserDTO::id, u -> u));

        Map<Long, CourseDTO> courseMap = courses.stream()
                .collect(Collectors.toMap(CourseDTO::id, c -> c));


        return topics.stream()
                .map(topic -> {
                    UserDTO user = userMap.get(topic.getUserId());
                    CourseDTO course = courseMap.get(topic.getCourseId());
                    Long count = repliesCount.stream()
                            .filter(r -> r.topicId().equals(topic.getId()))
                            .findFirst()
                            .map(ReplyCountDTO::count)
                            .orElse(0L);
                    return TopicDTO.fromEntity(topic, user, course, count);
                })
                .toList();
    }
}
