package com.williammedina.user_service.infrastructure.client;

import com.williammedina.user_service.domain.user.dto.UserFollowedTopicCountDTO;
import com.williammedina.user_service.domain.user.dto.UserTopicCountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "topic-service", path = "/internal/topic")
public interface TopicServiceClient {

    @GetMapping("/user/{userId}/count")
    UserTopicCountDTO getTopicCountByUser(@PathVariable Long userId);

    @GetMapping("/user/{userId}/followed/count")
    UserFollowedTopicCountDTO getUserFollowedTopicCount(@PathVariable Long userId);

}
