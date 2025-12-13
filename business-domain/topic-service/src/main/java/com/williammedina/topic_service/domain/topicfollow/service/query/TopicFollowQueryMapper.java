package com.williammedina.topic_service.domain.topicfollow.service.query;

import com.williammedina.topic_service.domain.topic.dto.CourseDTO;
import com.williammedina.topic_service.domain.topic.dto.ReplyCountDTO;
import com.williammedina.topic_service.domain.topic.dto.TopicDTO;
import com.williammedina.topic_service.domain.topic.dto.UserDTO;
import com.williammedina.topic_service.domain.topicfollow.dto.TopicFollowDetailsDTO;
import com.williammedina.topic_service.domain.topicfollow.dto.TopicFollowerDTO;
import com.williammedina.topic_service.domain.topicfollow.entity.TopicFollowEntity;
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
public class TopicFollowQueryMapper {

    private final UserServiceClient userServiceClient;
    private final CourseServiceClient courseServiceClient;
    private final ReplyServiceClient replyServiceClient;

    public List<TopicFollowDetailsDTO> mapTopicFollowsDetailsToDTOs(List<TopicFollowEntity> topicFollows) {

        List<Long> userIds = topicFollows.stream()
                .map(TopicFollowEntity::getUserId)
                .distinct()
                .toList();

        List<Long> courseIds = topicFollows.stream()
                .map(topicFollow -> topicFollow.getTopic().getCourseId())
                .distinct()
                .toList();

        List<UserDTO> users = userServiceClient.getUsersByIds(userIds);
        List<CourseDTO> courses = courseServiceClient.getCoursesByIds(courseIds);
        List<ReplyCountDTO> repliesCount = replyServiceClient.getReplyCountsByTopicIds(topicFollows.stream().map(t -> t.getTopic().getId()).toList());

        Map<Long, UserDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserDTO::id, u -> u));

        Map<Long, CourseDTO> courseMap = courses.stream()
                .collect(Collectors.toMap(CourseDTO::id, c -> c));

        return topicFollows.stream()
                .map(topicFollow -> {
                    UserDTO user = userMap.get(topicFollow.getUserId());
                    CourseDTO course = courseMap.get(topicFollow.getTopic().getCourseId());
                    Long count = repliesCount.stream()
                            .filter(r -> r.topicId().equals(topicFollow.getId()))
                            .findFirst()
                            .map(ReplyCountDTO::count)
                            .orElse(0L);
                    return toTopicFollowDetailsDTO(topicFollow, user, course, count);
                })
                .toList();
    }

    public List<TopicFollowerDTO> mapTopicFollowerToDTOs(List<TopicFollowEntity> topicFollows) {

        List<Long> userIds = topicFollows.stream()
                .map(TopicFollowEntity::getUserId)
                .distinct()
                .toList();

        List<UserDTO> users = userServiceClient.getUsersByIds(userIds);

        Map<Long, UserDTO> userMap = users.stream()
                .collect(Collectors.toMap(UserDTO::id, u -> u));

        return topicFollows.stream()
                .map(topicFollow -> {
                    UserDTO user = userMap.get(topicFollow.getUserId());
                    return toTopicFollowDTO(topicFollow, user);
                })
                .toList();
    }

    private TopicFollowDetailsDTO toTopicFollowDetailsDTO(TopicFollowEntity topicFollow, UserDTO user, CourseDTO course, Long repliesCount) {
        return new TopicFollowDetailsDTO(TopicDTO.fromEntity(topicFollow.getTopic(), user, course, repliesCount), topicFollow.getFollowedAt());
    }

    private TopicFollowerDTO toTopicFollowDTO(TopicFollowEntity topicFollow, UserDTO user) {
        return new TopicFollowerDTO(user, topicFollow.getFollowedAt());
    }
}
