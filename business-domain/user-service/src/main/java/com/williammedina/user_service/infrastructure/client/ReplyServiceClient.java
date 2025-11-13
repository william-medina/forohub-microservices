package com.williammedina.user_service.infrastructure.client;

import com.williammedina.user_service.domain.user.dto.UserReplyCountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "reply-service", path = "/internal/reply")
public interface ReplyServiceClient {

    @GetMapping("/user/{userId}/count")
    UserReplyCountDTO getUserReplyCount(@PathVariable Long userId);

}
